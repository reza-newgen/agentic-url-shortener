package com.schwab.agentic.orchestration;


import java.util.*;

public record ExecutionPlan(List<Step> steps) {
    public record Step(String id, List<String> dependsOn, boolean humanApproval) {
    }

    public static ExecutionPlan standard() {
        return new ExecutionPlan(List.of(
                new Step("analysis", List.of(), false), new Step("planning", List.of("analysis"), false), new Step("architecture", List.of("planning"), true), new Step("implementation", List.of("architecture"), true),
                new Step("testing", List.of("implementation"), false), new Step("security", List.of("implementation"), false), new Step("documentation", List.of("implementation"), false),
                new Step("review", List.of("testing", "security", "documentation"), true)));
    }
}
