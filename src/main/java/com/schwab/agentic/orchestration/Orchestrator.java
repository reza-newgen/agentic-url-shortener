package com.schwab.agentic.orchestration;


import org.springframework.web.bind.annotation.*;
import com.schwab.agentic.governance.*;

import java.util.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@RestController
@RequestMapping("/api/workflows")
public class Orchestrator {
    private final WorkflowEngine engine;
    private final PolicyEngine policy;
    private final AuditService audit;
    private final AutomaticWorkflowRunner runner;

    public Orchestrator(WorkflowEngine engine, PolicyEngine policy, AuditService audit, AutomaticWorkflowRunner runner) {
        this.engine = engine;
        this.policy = policy;
        this.audit = audit;
        this.runner = runner;
    }

    public record NewRun(@NotBlank String requirement, @NotBlank String scenario) {
    }

    public record Change(@NotBlank String requirement) {
    }

    @PostMapping
    public WorkflowRun create(@Valid @RequestBody NewRun input) {
        policy.validateRequirement(input.requirement());
        if (!Set.of("greenfield", "brownfield", "ambiguous").contains(input.scenario()))
            throw new IllegalArgumentException("Bad scenario");
        WorkflowRun run = engine.create(input.requirement(), input.scenario());
        runner.requestExecution(run.id);
        return run;
    }

    @GetMapping("/{id}")
    public WorkflowRun get(@PathVariable UUID id) {
        return engine.get(id);
    }

    @GetMapping("/{id}/tasks")
    public List<WorkflowTask> tasks(@PathVariable UUID id) {
        return engine.tasks(id);
    }

    @GetMapping("/{id}/audit")
    public List<com.schwab.agentic.governance.AuditEvent> audit(@PathVariable UUID id) {
        return audit.events(id);
    }

    @PostMapping("/{id}/approve/{step}")
    public WorkflowRun approve(@PathVariable UUID id, @PathVariable String step, java.security.Principal principal) {
        WorkflowRun run = engine.approve(id, step, principal.getName());
        runner.requestExecution(id);
        return run;
    }

    @PostMapping("/{id}/stop")
    public WorkflowRun stop(@PathVariable UUID id) {
        return engine.stop(id);
    }

    @PostMapping("/{id}/rollback")
    public WorkflowRun rollback(@PathVariable UUID id) {
        return engine.rollback(id);
    }

    @PostMapping("/{id}/replan")
    public WorkflowRun replan(@PathVariable UUID id, @Valid @RequestBody Change input) {
        policy.validateRequirement(input.requirement());
        WorkflowRun run = engine.replan(id, input.requirement());
        runner.requestExecution(id);
        return run;
    }

    @PostMapping("/{id}/release")
    public WorkflowRun release(@PathVariable UUID id, java.security.Principal principal) {
        return engine.release(id, principal.getName());
    }
}
