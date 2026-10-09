package com.schwab.agentic.orchestration;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** Resume persisted workflows after application restarts. Approval gates stay paused. */
@Component
public class WorkflowRecovery {
    private final WorkflowRepository repository;
    private final AutomaticWorkflowRunner runner;
    public WorkflowRecovery(WorkflowRepository repository, AutomaticWorkflowRunner runner) {
        this.repository = repository;
        this.runner = runner;
    }
    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        repository.findAll().stream()
                .filter(r -> r.state == WorkflowState.CREATED || r.state == WorkflowState.RUNNING
                        || r.state == WorkflowState.ANALYZING || r.state == WorkflowState.PLANNING
                        || r.state == WorkflowState.REVIEWING)
                .forEach(r -> runner.requestExecution(r.id));
    }
}
