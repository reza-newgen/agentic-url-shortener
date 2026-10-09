package com.schwab.agentic.orchestration;


import com.schwab.agentic.agents.*;
import com.schwab.agentic.governance.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.concurrent.*;
import java.time.*;

@Service
public class WorkflowEngine {
    private final WorkflowRepository runs;
    private final WorkflowTaskRepository tasks;
    private final AuditService audit;
    private final RequirementAgent analyst;
    private final PlannerAgent planner;
    private final ArchitectureAgent architect;
    private final DeveloperAgent developer;
    private final TestAgent tester;
    private final SecurityAgent security;
    private final DocumentationAgent docs;
    private final ReviewAgent reviewer;
    private final DependencyGraph graph;
    private final RetryPolicy retries;
    private final RiskAssessment risks;
    @Value("${agentic.task-timeout-seconds:300}")
    private long taskTimeoutSeconds;

    public WorkflowEngine(WorkflowRepository runs, WorkflowTaskRepository tasks, AuditService audit, RequirementAgent analyst, PlannerAgent planner, ArchitectureAgent architect, DeveloperAgent developer, TestAgent tester, SecurityAgent security, DocumentationAgent docs, ReviewAgent reviewer, DependencyGraph graph, RetryPolicy retries, RiskAssessment risks) {
        this.runs = runs;
        this.tasks = tasks;
        this.audit = audit;
        this.analyst = analyst;
        this.planner = planner;
        this.architect = architect;
        this.developer = developer;
        this.tester = tester;
        this.security = security;
        this.docs = docs;
        this.reviewer = reviewer;
        this.graph = graph;
        this.retries = retries;
        this.risks = risks;
    }

    @Transactional
    public WorkflowRun create(String requirement, String scenario) {
        WorkflowRun r = new WorkflowRun();
        r.id = UUID.randomUUID();
        r.requirement = requirement;
        r.scenario = scenario;
        r.state = WorkflowState.CREATED;
        r.planJson = "analysis>planning>architecture>implementation>[testing|security|documentation]>review";
        runs.save(r);
        for (var step : ExecutionPlan.standard().steps()) {
            WorkflowTask t = new WorkflowTask();
            t.runId = r.id;
            t.taskKey = step.id();
            t.taskState = "PENDING";
            tasks.save(t);
        }
        audit.write(r.id, "system", "CREATED", scenario);
        return r;
    }

    public WorkflowRun get(UUID id) {
        return runs.findById(id).orElseThrow(() -> new NoSuchElementException("Unknown run"));
    }

    public List<WorkflowTask> tasks(UUID id) {
        get(id);
        return tasks.findByRunIdOrderByIdAsc(id);
    }

    public synchronized WorkflowRun advance(UUID id) {
        WorkflowRun r = get(id);
        if (List.of(WorkflowState.STOPPED, WorkflowState.FAILED, WorkflowState.COMPLETED, WorkflowState.ROLLED_BACK).contains(r.state))
            throw new IllegalStateException("Terminal workflow");
        List<WorkflowTask> all = tasks(id);
        Map<String, WorkflowTask> map = new HashMap<>();
        for (var task : all) map.put(task.taskKey, task);
        for (var level : graph.levels(ExecutionPlan.standard())) {
            List<ExecutionPlan.Step> ready = level.stream().filter(s -> (map.get(s.id()).taskState.equals("PENDING") || map.get(s.id()).taskState.equals("APPROVED")) && s.dependsOn().stream().allMatch(dep -> map.get(dep).taskState.equals("DONE"))).toList();
            if (ready.isEmpty()) continue;
            if (ready.stream().anyMatch(s -> s.humanApproval() && map.get(s.id()).taskState.equals("PENDING"))) {
                // Approval is required before architecture, implementation or release review.
                for (var s : ready)
                    if (s.humanApproval()) {
                        map.get(s.id()).taskState = "WAITING_APPROVAL";
                        tasks.save(map.get(s.id()));
                    }
                r.state = WorkflowState.AWAITING_APPROVAL;
                runs.save(r);
                audit.write(id, "engine", "APPROVAL_GATE", "Pending: " + ready.stream().map(ExecutionPlan.Step::id).toList());
                return r;
            }
            executeLevel(r, ready, map);
            return get(id);
        }
        if (all.stream().allMatch(t -> t.taskState.equals("DONE"))) {
            r.state = WorkflowState.READY_FOR_RELEASE;
            r.report = report(r);
            runs.save(r);
            audit.write(id, "engine", "RELEASE_READY", "Evidence review pending final human release");
        }
        return r;
    }


    private void executeLevel(
            WorkflowRun r,
            List<ExecutionPlan.Step> level,
            Map<String, WorkflowTask> map) {

        r.state = WorkflowState.RUNNING;
        runs.save(r);

        ExecutorService executor =
                Executors.newVirtualThreadPerTaskExecutor();

        Map<String, Future<String>> pending =
                new LinkedHashMap<>();

        try {
            // Submit independent agents concurrently.
            for (var step : level) {
                pending.put(
                        step.id(),
                        executor.submit(() ->
                                invoke(step.id(), r.requirement, map)
                        )
                );
            }

            for (var entry : pending.entrySet()) {
                WorkflowTask task = map.get(entry.getKey());

                try {
                    String result = entry.getValue().get(
                            taskTimeoutSeconds,
                            TimeUnit.SECONDS
                    );

                    task.artifact = result;
                    task.taskState = "DONE";
                    tasks.save(task);

                    audit.write(
                            r.id,
                            "agent:" + entry.getKey(),
                            "DONE",
                            result.substring(
                                    0, Math.min(300, result.length())
                            )
                    );

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    handleAgentFailure(
                            r, task, entry.getKey(), ex, false
                    );
                    cancelPending(pending);
                    return;

                } catch (TimeoutException | ExecutionException ex) {
                    boolean retryable =
                            !(ex instanceof ExecutionException
                                    && ex.getCause() instanceof
                                    IllegalArgumentException);

                    handleAgentFailure(
                            r, task, entry.getKey(), ex, retryable
                    );

                    cancelPending(pending);
                    return;
                }
            }

        } finally {
            executor.shutdownNow();
        }

        r.state = WorkflowState.RUNNING;
        runs.save(r);
    }

    private void handleAgentFailure(
            WorkflowRun run,
            WorkflowTask task,
            String stage,
            Exception exception,
            boolean retryable) {

        task.retries++;

        boolean retry =
                retryable && retries.canRetry(task.retries);

        task.taskState = retry ? "PENDING" : "FAILED";
        tasks.save(task);

        run.state = retry
                ? WorkflowState.RUNNING
                : WorkflowState.FAILED;

        runs.save(run);

        audit.write(
                run.id,
                "engine",
                retry ? "RETRY" : "AGENT_FAILED",
                stage + ": " + exception.getClass().getSimpleName()
                        + ", attempt=" + task.retries
        );
    }

    private void cancelPending(
            Map<String, Future<String>> pending) {

        for (Future<String> future : pending.values()) {
            if (!future.isDone()) {
                future.cancel(true);
            }
        }
    }


    private String invoke(String stage, String requirement, Map<String, WorkflowTask> context) {
        String input = "Requirement: " + requirement + "\nArchitecture context: " + (context.get("architecture") != null ? String.valueOf(context.get("architecture").artifact) : "");
        return switch (stage) {
            case "analysis" -> analyst.execute(input);
            case "planning" -> planner.execute(input);
            case "architecture" -> architect.execute(input);
            case "implementation" -> developer.execute(input);
            case "testing" -> tester.execute(input);
            case "security" -> security.execute(input);
            case "documentation" -> docs.execute(input);
            case "review" -> reviewer.execute(input);
            default -> throw new IllegalArgumentException("stage");
        };
    }

    @Transactional
    public synchronized WorkflowRun approve(UUID id, String step, String actor) {
        WorkflowRun r = get(id);
        WorkflowTask t = tasks.findByRunIdAndTaskKey(id, step).orElseThrow();
        if (!t.taskState.equals("WAITING_APPROVAL")) throw new IllegalStateException("Step not awaiting approval");
        t.taskState = "APPROVED";
        tasks.save(t);
        r.state = WorkflowState.RUNNING;
        runs.save(r);
        audit.write(id, actor, "APPROVED", step);
        return r;
    }

    @Transactional
    public synchronized void fail(UUID id, String message) {
        WorkflowRun r = get(id);
        if (r.state == WorkflowState.STOPPED || r.state == WorkflowState.ROLLED_BACK || r.state == WorkflowState.COMPLETED)
            return;
        r.state = WorkflowState.FAILED;
        runs.save(r);
        audit.write(id, "engine", "DRIVER_FAILED", String.valueOf(message));
    }

    @Transactional
    public synchronized WorkflowRun stop(UUID id) {
        WorkflowRun r = get(id);
        r.state = WorkflowState.STOPPED;
        runs.save(r);
        audit.write(id, "reviewer", "SAFE_STOP", "Manual safe stop");
        return r;
    }

    @Transactional
    public synchronized WorkflowRun replan(UUID id, String changedRequirement) {
        WorkflowRun r = get(id);
        if (r.state == WorkflowState.COMPLETED) throw new IllegalStateException("Already released");
        r.requirement = changedRequirement;
        r.revision++;
        r.state = WorkflowState.CREATED;
        r.report = null;
        for (var t : tasks.findByRunIdOrderByIdAsc(id)) {
            t.taskState = "PENDING";
            t.artifact = null;
            t.retries = 0;
            tasks.save(t);
        }
        runs.save(r);
        audit.write(id, "reviewer", "REPLAN", "Revision " + r.revision);
        return r;
    }

    @Transactional
    public synchronized WorkflowRun rollback(UUID id) {
        WorkflowRun r = get(id);
        if (r.state == WorkflowState.COMPLETED) throw new IllegalStateException("Cannot undo released production code");
        r.state = WorkflowState.ROLLED_BACK;
        for (var t : tasks.findByRunIdOrderByIdAsc(id)) {
            t.artifact = null;
            tasks.save(t);
        }
        runs.save(r);
        audit.write(id, "reviewer", "ROLLBACK", "Discarded proposed artifacts, no production rollback claimed");
        return r;
    }

    @Transactional
    public synchronized WorkflowRun release(UUID id, String actor) {
        WorkflowRun r = get(id);
        if (r.state != WorkflowState.READY_FOR_RELEASE) throw new IllegalStateException("Release gate not passed");
        r.state = WorkflowState.COMPLETED;
        runs.save(r);
        audit.write(id, actor, "RELEASE_APPROVED", "Review complete; no automatic deployment");
        return r;
    }

    private String report(WorkflowRun r) {
        return "RUN " + r.id + "\nRequirement: " + r.requirement + "\nRisk: " + risks.assess(r.requirement) + "\nArtifacts: " + tasks.findByRunIdOrderByIdAsc(r.id).stream().map(t -> t.taskKey + "=" + t.taskState).toList() + "\nLimitations: proposed engineering outputs only; no automatic repository merge or production deployment.";
    }
}
