package com.schwab.agentic.agents;


import org.springframework.stereotype.Component;

@Component
public class DeveloperAgent {
    private final AgentSupport support;

    public DeveloperAgent(AgentSupport s) {
        support = s;
    }

    public String execute(String input) {
        return support.generate("Java developer", input, "Produce reviewable Java changes. No direct unrestricted execution.");
    }
}
