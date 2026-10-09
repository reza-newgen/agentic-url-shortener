package com.schwab.agentic.agents;


import org.springframework.stereotype.Component;

@Component
public class SecurityAgent {
    private final AgentSupport support;

    public SecurityAgent(AgentSupport s) {
        support = s;
    }

    public String execute(String input) {
        return support.generate("application security reviewer", input, "Check validation, auth, injection risks, credentials and change permissions.");
    }
}
