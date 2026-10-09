package com.schwab.agentic.governance;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class AuditService {
    private final AuditRepository repository;

    public AuditService(AuditRepository r) {
        repository = r;
    }

    @Transactional
    public void write(UUID id, String actor, String action, String details) {
        AuditEvent e = new AuditEvent();
        e.runId = id;
        e.actor = actor;
        e.action = action;
        e.details = details;
        e.eventTime = Instant.now();
        repository.save(e);
    }

    public List<AuditEvent> events(UUID id) {
        return repository.findByRunIdOrderByIdAsc(id);
    }
}
