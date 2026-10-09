package com.schwab.agentic.governance;


import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ApprovalService {
    private final AuditService audit;

    public ApprovalService(AuditService audit) {
        this.audit = audit;
    }

    public void approved(UUID id, String reviewer) {
        audit.write(id, reviewer, "APPROVED", "Manual reviewer approval");
    }
}
