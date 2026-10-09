package com.schwab.agentic.orchestration;


import java.util.*;

import org.springframework.stereotype.Component;

@Component
public class DependencyGraph {
    public List<List<ExecutionPlan.Step>> levels(ExecutionPlan plan) {
        Map<String, ExecutionPlan.Step> pending = new LinkedHashMap<>();
        for (var s : plan.steps()) {
            if (pending.put(s.id(), s) != null) throw new IllegalArgumentException("Duplicate node");
        }
        for (var s : plan.steps())
            for (String dep : s.dependsOn())
                if (!pending.containsKey(dep)) throw new IllegalArgumentException("Unknown dependency: " + dep);
        Set<String> done = new HashSet<>();
        List<List<ExecutionPlan.Step>> levels = new ArrayList<>();
        while (!pending.isEmpty()) {
            List<ExecutionPlan.Step> level = pending.values().stream().filter(s -> done.containsAll(s.dependsOn())).toList();
            if (level.isEmpty()) throw new IllegalStateException("Cycle in task graph");
            levels.add(level);
            for (var s : level) {
                pending.remove(s.id());
                done.add(s.id());
            }
        }
        return levels;
    }
}
