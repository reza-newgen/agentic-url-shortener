package com.schwab.agentic.orchestration;


import org.springframework.stereotype.Component;

@Component
public class RollbackManager {
    public void rollback(java.nio.file.Path workspace) {
        if (!java.nio.file.Files.exists(workspace)) return;
        try (var paths = java.nio.file.Files.walk(workspace)) {
            for (var path : paths.sorted(java.util.Comparator.reverseOrder()).toList())
                java.nio.file.Files.deleteIfExists(path);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Workspace rollback failed", e);
        }
    }
}
