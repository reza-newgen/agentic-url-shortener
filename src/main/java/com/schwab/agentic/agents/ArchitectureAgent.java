package com.schwab.agentic.agents;


import org.springframework.stereotype.Component;

@Component
public class ArchitectureAgent {
    private final AgentSupport support;

    public ArchitectureAgent(AgentSupport s) {
        support = s;
    }

    public String execute(String input) {
        return support.generate("software architect", input, "Analyze Spring REST, services, JPA, schema, Redis and control boundaries.");
    }
}
