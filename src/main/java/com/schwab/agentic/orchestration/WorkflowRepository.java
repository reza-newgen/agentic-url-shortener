package com.schwab.agentic.orchestration;


import org.springframework.data.jpa.repository.JpaRepository;import java.util.UUID;
public interface WorkflowRepository extends JpaRepository<WorkflowRun,UUID>{}