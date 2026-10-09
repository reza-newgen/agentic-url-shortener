package com.schwab.agentic.agents;


import org.springframework.stereotype.Component;

@Component
public class ReviewAgent {
    private final AgentSupport support;

    public ReviewAgent(AgentSupport s) {
        support = s;
    }

    public String execute(String input) {
        return support.generate("release reviewer", input, "Summarize evidence, unresolved risks and readiness. Human owns release decision.");
    }
}
