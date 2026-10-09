package com.schwab.agentic.metrics;


import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Component;

@Component
public class WorkflowMetrics {
    private final MeterRegistry registry;

    public WorkflowMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    public void record(String event) {
        registry.counter("agentic_workflow_event_total", "event", event).increment();
    }
}
