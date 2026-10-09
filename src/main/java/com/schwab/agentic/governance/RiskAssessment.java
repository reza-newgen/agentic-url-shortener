package com.schwab.agentic.governance;


import org.springframework.stereotype.Component;

@Component
public class RiskAssessment {
    public String assess(String requirement) {
        return "Review generated changes; protect data and credentials; run tests; approve changes before release. Scope: " + requirement.substring(0, Math.min(120, requirement.length()));
    }
}