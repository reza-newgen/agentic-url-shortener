package com.schwab.agentic;
import org.junit.jupiter.api.Test;import org.testcontainers.containers.PostgreSQLContainer;import org.testcontainers.junit.jupiter.*;import static org.junit.jupiter.api.Assertions.*;
@Testcontainers(disabledWithoutDocker=true) class DatabaseIntegrationTest {
 @Container static PostgreSQLContainer<?> database=new PostgreSQLContainer<>("postgres:16-alpine");
 @Test void postgresStarts(){assertTrue(database.isRunning());}
}
