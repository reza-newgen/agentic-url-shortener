package com.schwab.agentic.agents;


import org.springframework.stereotype.Component;

@Component
public class RequirementAgent {

    private final AgentSupport support;

    public RequirementAgent(AgentSupport support) {
        this.support = support;
    }

    public String execute(String input) {

        String prompt = """
                Act as a Senior Requirements Analyst
                for an Agentic Software Engineering System.
                
                Analyze the following requirement and produce
                a structured Software Requirements Specification.
                
                PROJECT TECHNOLOGY STACK:
                - Java 21
                - Spring Boot
                - Spring Data JPA / Hibernate
                - PostgreSQL
                - Redis
                - Spring Security
                - Spring AI with Ollama
                - REST APIs
                - JUnit 5, Mockito and Testcontainers
                - Flyway
                - Maven and Docker
                - OpenAPI / Swagger
                
                Do not propose backend implementations using
                Python, Flask, Node.js, Express.js or TypeScript.
                
                OUTPUT FORMAT:
                
                1. Requirement Summary
                   Explain the business objective.
                
                2. Functional Requirements
                   List the expected system behaviors.
                
                3. Non-Functional Requirements
                   Address performance, security,
                   scalability and reliability.
                
                4. Technical Requirements
                   Describe the Java/Spring Boot components
                   needed to satisfy the requirement.
                
                5. Acceptance Criteria
                   Provide clear, testable conditions.
                
                6. Dependencies
                   Identify database, cache, API and
                   security dependencies.
                
                7. Risks and Assumptions
                   Identify implementation risks and
                   assumptions requiring validation.
                
                8. Ambiguities
                   List unanswered questions requiring
                   clarification. Do not invent decisions
                   for unresolved business requirements.
                
                9. Definition of Done
                   Define what must be verified before
                   the requirement is considered complete.
                
                IMPORTANT:
                - Follow the existing project architecture.
                - Do not claim implementation is complete.
                - Do not claim tests have passed.
                - Produce reviewable engineering requirements.
                
                USER REQUIREMENT:
                """ + input;

        String fallback = """
                REQUIREMENTS ANALYSIS UNAVAILABLE
                
                The live requirement analysis could not be
                generated.
                
                Requested requirement:
                """ + input;

        return support.generate(
                "requirements analyst",
                prompt,
                fallback
        );
    }

}
