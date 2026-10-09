package com.schwab.agentic.orchestration;


import org.springframework.stereotype.Component;

@Component
public class RetryPolicy {
    public int maximumAttempts() {
        return 2;
    }

    public boolean canRetry(int attempted) {
        return attempted < maximumAttempts();
    }
}