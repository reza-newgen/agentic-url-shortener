package com.schwab.agentic.governance;


import org.springframework.stereotype.Component;

@Component
public class PolicyEngine {
    public void validateRequirement(String value) {
        if (value == null || value.isBlank() || value.length() > 8000)
            throw new IllegalArgumentException("Requirement must contain 1..8000 characters");
    }

    public void validateArtifactPath(String path) {
        if (!path.matches("[A-Za-z0-9_./-]{1,180}") || path.contains("..") || path.startsWith("/") || !path.endsWith(".java"))
            throw new SecurityException("Unsafe generated path");
    }
}
