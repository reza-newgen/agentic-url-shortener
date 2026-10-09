package com.schwab.agentic.orchestration;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface WorkflowTaskRepository extends JpaRepository<WorkflowTask, Long> {
    List<WorkflowTask> findByRunIdOrderByIdAsc(UUID runId);

    Optional<WorkflowTask> findByRunIdAndTaskKey(UUID runId, String taskKey);

    void deleteByRunId(UUID runId);
}