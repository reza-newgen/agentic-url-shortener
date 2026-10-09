package com.schwab.agentic.orchestration;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AutomaticWorkflowRunnerTest {
    @Test
    void advancesWithoutAnyManualApiCallsUntilApprovalGate() throws Exception {
        WorkflowEngine engine = mock(WorkflowEngine.class);
        UUID id = UUID.randomUUID();
        WorkflowRun created = run(id, WorkflowState.CREATED);
        WorkflowRun running = run(id, WorkflowState.RUNNING);
        WorkflowRun approval = run(id, WorkflowState.AWAITING_APPROVAL);
        CountDownLatch reachedGate = new CountDownLatch(1);
        AtomicInteger calls = new AtomicInteger();
        when(engine.get(id)).thenAnswer(inv -> calls.get() < 2 ? created : approval);
        when(engine.advance(id)).thenAnswer(inv -> {
            int count = calls.incrementAndGet();
            if (count == 2) { reachedGate.countDown(); return approval; }
            return running;
        });
        AutomaticWorkflowRunner runner = new AutomaticWorkflowRunner(engine);
        try {
            runner.requestExecution(id);
            assertTrue(reachedGate.await(5, TimeUnit.SECONDS));
            verify(engine, times(2)).advance(id);
        } finally { runner.shutdown(); }
    }

    @Test
    void doesNotAdvanceAlreadyPausedWorkflow() throws Exception {
        WorkflowEngine engine = mock(WorkflowEngine.class);
        UUID id = UUID.randomUUID();
        when(engine.get(id)).thenReturn(run(id, WorkflowState.AWAITING_APPROVAL));
        CountDownLatch observed = new CountDownLatch(1);
        when(engine.get(id)).thenAnswer(inv -> { observed.countDown(); return run(id, WorkflowState.AWAITING_APPROVAL); });
        AutomaticWorkflowRunner runner = new AutomaticWorkflowRunner(engine);
        try {
            runner.requestExecution(id);
            assertTrue(observed.await(5, TimeUnit.SECONDS));
            verify(engine, never()).advance(id);
        } finally { runner.shutdown(); }
    }

    private static WorkflowRun run(UUID id, WorkflowState state) {
        WorkflowRun result = new WorkflowRun();
        result.id = id;
        result.state = state;
        return result;
    }
}
