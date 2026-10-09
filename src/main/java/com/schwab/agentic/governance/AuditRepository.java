package com.schwab.agentic.governance;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface AuditRepository extends JpaRepository<AuditEvent, Long> {
    List<AuditEvent> findByRunIdOrderByIdAsc(UUID runId);
}