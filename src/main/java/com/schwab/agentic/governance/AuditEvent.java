package com.schwab.agentic.governance;


import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_event")
public class AuditEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "run_id")
    public UUID runId;
    @Column(name = "event_time")
    public Instant eventTime;
    public String actor;
    public String action;
    @Column(columnDefinition = "text")
    public String details;
}
