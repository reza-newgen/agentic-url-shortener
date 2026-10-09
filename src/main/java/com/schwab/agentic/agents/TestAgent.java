package com.schwab.agentic.agents;


import org.springframework.stereotype.Component;

@Component
public class TestAgent {
    private final AgentSupport support;

    public TestAgent(AgentSupport s) {
        support = s;
    }

    public String execute(String input) {
        return support.generate("quality engineer", input, "Unit, integration, edge cases and regression verification.");
    }
}
