package com.schwab.agentic.agents;


import org.springframework.stereotype.Component;

@Component
public class DocumentationAgent {
    private final AgentSupport support;

    public DocumentationAgent(AgentSupport s) {
        support = s;
    }

    public String execute(String input) {
        return support.generate("technical writer", input, "Provide API specification, operations and architecture notes.");
    }
}
