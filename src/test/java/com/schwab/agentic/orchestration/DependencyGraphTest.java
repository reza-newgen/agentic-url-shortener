package com.schwab.agentic.orchestration;
import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;import java.util.*;
class DependencyGraphTest {
 @Test void parallelReviewBranches(){var levels=new DependencyGraph().levels(ExecutionPlan.standard());assertTrue(levels.stream().anyMatch(l->l.stream().map(ExecutionPlan.Step::id).collect(java.util.stream.Collectors.toSet()).containsAll(Set.of("testing","security","documentation"))));}
 @Test void cycleFails(){var plan=new ExecutionPlan(List.of(new ExecutionPlan.Step("a",List.of("b"),false),new ExecutionPlan.Step("b",List.of("a"),false)));assertThrows(IllegalStateException.class,()->new DependencyGraph().levels(plan));}
}
