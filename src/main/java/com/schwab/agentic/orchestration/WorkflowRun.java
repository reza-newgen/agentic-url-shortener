package com.schwab.agentic.orchestration;


import jakarta.persistence.*;

import java.time.*;
import java.util.*;

@Entity
@Table(name = "workflow_run")
public class WorkflowRun {
    @Id
    public UUID id;
    @Column(columnDefinition = "text", nullable = false)
    public String requirement;
    public String scenario;
    @Enumerated(EnumType.STRING)
    public WorkflowState state;
    public int revision;
    @Column(name = "plan_json", columnDefinition = "text")
    public String planJson;
    @Column(columnDefinition = "text")
    public String report;
    @Column(name = "created_at")
    public Instant createdAt;
    @Column(name = "updated_at")
    public Instant updatedAt;

    @PrePersist
    public void created() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void updated() {
        updatedAt = Instant.now();
    }
}
