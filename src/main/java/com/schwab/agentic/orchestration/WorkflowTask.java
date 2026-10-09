package com.schwab.agentic.orchestration;


import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "workflow_task", uniqueConstraints = @UniqueConstraint(columnNames = {"run_id", "task_key"}))
public class WorkflowTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "run_id")
    public UUID runId;
    @Column(name = "task_key")
    public String taskKey;
    @Column(name = "task_state")
    public String taskState;
    @Column(columnDefinition = "text")
    public String artifact;
    public int retries;
}
