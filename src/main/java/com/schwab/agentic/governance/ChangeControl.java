package com.schwab.agentic.governance;


import org.springframework.stereotype.Component;

@Component
public class ChangeControl {
    public boolean requiresApproval(String stage) {
        return stage.equals("architecture") || stage.equals("implementation") || stage.equals("review");
    }
}