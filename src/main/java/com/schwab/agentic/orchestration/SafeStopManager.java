package com.schwab.agentic.orchestration;


import org.springframework.stereotype.Component;

@Component
public class SafeStopManager {
    public void check(boolean stopped) {
        if (stopped) throw new IllegalStateException("Workflow safe-stopped");
    }
}