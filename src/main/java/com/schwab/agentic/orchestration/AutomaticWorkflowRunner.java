package com.schwab.agentic.orchestration;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Automatically drives each run until human input or a terminal/release state is reached. */
@Service
public class AutomaticWorkflowRunner {
    private static final Logger log = LoggerFactory.getLogger(AutomaticWorkflowRunner.class);
    private static final int MAX_TRANSITIONS_PER_PASS = 40;
    private final WorkflowEngine engine;
    private final ExecutorService executor;
    private final Set<UUID> active = ConcurrentHashMap.newKeySet();
    private final Set<UUID> requested = ConcurrentHashMap.newKeySet();

    public AutomaticWorkflowRunner(WorkflowEngine engine) {
        this.engine = engine;
        this.executor = Executors.newFixedThreadPool(4, Thread.ofVirtual().name("workflow-driver-", 0).factory());
    }

    /** Safe to call repeatedly; coalesces duplicate triggers for the same workflow. */
    public void requestExecution(UUID workflowId) {
        requested.add(workflowId);
        schedule(workflowId);
    }

    private void schedule(UUID id) {
        if (active.add(id)) {
            executor.execute(() -> drive(id));
        }
    }

    private void drive(UUID id) {
        try {
            do {
                requested.remove(id);
                for (int transition = 0; transition < MAX_TRANSITIONS_PER_PASS; transition++) {
                    WorkflowState before = engine.get(id).state;
                    if (pausedOrTerminal(before)) break;
                    WorkflowState after = engine.advance(id).state;
                    if (pausedOrTerminal(after)) break;
                }
                // A new approval/replan trigger may have arrived while this worker was running.
            } while (requested.contains(id));
        } catch (Exception e) {
            log.error("Automatic workflow {} failed", id, e);
            try { engine.fail(id, e.getMessage()); }
            catch (Exception persistError) { log.error("Failed to persist workflow failure for {}", id, persistError); }
        } finally {
            active.remove(id);
            if (requested.contains(id)) schedule(id);
        }
    }

    private boolean pausedOrTerminal(WorkflowState state) {
        return state == WorkflowState.AWAITING_APPROVAL
            || state == WorkflowState.READY_FOR_RELEASE
            || state == WorkflowState.COMPLETED
            || state == WorkflowState.FAILED
            || state == WorkflowState.STOPPED
            || state == WorkflowState.ROLLED_BACK;
    }

    @PreDestroy
    public void shutdown() { executor.shutdown(); }
}
