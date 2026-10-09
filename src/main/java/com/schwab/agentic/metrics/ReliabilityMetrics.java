package com.schwab.agentic.metrics;


import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Component;

@Component
public class ReliabilityMetrics {
    public ReliabilityMetrics(MeterRegistry registry) {
        registry.counter("agentic_workflow_started_total");
        registry.counter("agentic_workflow_retries_total");
        registry.counter("agentic_workflow_rollbacks_total");
        registry.timer("agentic_workflow_execution_seconds");
    }
}
