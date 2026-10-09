package com.schwab.agentic.agents;


import org.springframework.stereotype.Component;

@Component
public class PlannerAgent {
    private final AgentSupport support;

    public PlannerAgent(AgentSupport support) {
        this.support = support;
    }

    public String execute(String input) {

        String prompt = """
            Act as a Senior Java Technical Lead and
            Agentic SDLC Engineering Planner.

            Your responsibility is to transform the
            provided requirement and context into a
            structured, executable engineering plan.

            MANDATORY TECHNOLOGY STACK:
            - Java 21
            - Spring Boot
            - Spring AI with Ollama
            - Spring Data JPA / Hibernate
            - PostgreSQL
            - Redis
            - REST APIs
            - Spring Security
            - JUnit 5 and Mockito
            - Testcontainers
            - Maven
            - Docker
            - Flyway
            - OpenAPI / Swagger
            - Micrometer / Actuator

            STRICT RULES:
            1. Use Java 21 and Spring Boot for backend work.
            2. Do not propose Python, Flask, Node.js,
               Express.js or TypeScript implementations.
            3. Follow the existing project architecture.
            4. Break the requirement into small,
               independently reviewable tasks.
            5. Identify dependencies between tasks.
            6. Identify tasks that can run concurrently.
            7. Define explicit human-approval checkpoints.
            8. Include validation and acceptance criteria.
            9. Do not claim code or tests have been executed.
            10. Flag ambiguities that require clarification.

            OUTPUT FORMAT:

            1. Requirement Understanding
               Summarize the objective and constraints.

            2. Task Decomposition
               For each task, provide:
               - Task ID
               - Task name
               - Responsible agent
               - Description
               - Dependencies
               - Expected deliverable
               - Acceptance criteria

            3. Dependency Graph
               Use this workflow structure:

               analysis
                   |
               planning
                   |
               architecture
                   |
               implementation
                   |
               +---------+----------+
               |         |          |
             testing   security  documentation
               |         |          |
               +---------+----------+
                         |
                       review

            4. Human Approval Gates
               - Architecture approval
               - Implementation approval
               - Final review approval

            5. Parallel Execution Strategy
               Explain which tasks can execute
               concurrently after implementation.

            6. Risk Assessment
               Identify dependencies, uncertainties,
               technical risks and mitigation steps.

            7. Testing Strategy
               Include JUnit 5, Mockito and
               Testcontainers-based testing.

            8. Security Strategy
               Include Spring Security,
               input validation and authorization.

            9. Documentation Strategy
               Include OpenAPI specifications,
               API examples and README updates.

            10. Definition of Done
                Define the criteria for a
                reviewable engineering outcome.

            IMPORTANT:
            This is a planning task.
            Do not generate an entire application.
            Do not claim implementation, testing,
            security scanning or deployment occurred.

            INPUT REQUIREMENT AND CONTEXT:
            """ + input;

        String fallback = """
            ENGINEERING PLANNING UNAVAILABLE

            Proposed stages:
            analysis -> planning -> architecture ->
            implementation ->
            [testing | security | documentation] ->
            review

            No AI-generated engineering plan
            was produced.
            """;

        return support.generate(
                "engineering planner",
                prompt,
                fallback
        );
    }
}
