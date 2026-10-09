package com.schwab.agentic;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Agentic SDLC Orchestrator - URL Shortener",
                version = "1.0.0",
                description = "Schwab Agentic Software Engineering System "
                        + "using Java 21, Spring Boot, Spring AI and Ollama"
        )
)
public class AgenticApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgenticApplication.class, args);
    }
}
