package com.schwab.agentic.orchestration;


public record WorkflowContext(java.util.UUID runId, String requirement, String scenario, int revision) {
}