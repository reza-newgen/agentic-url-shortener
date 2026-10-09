# Setup

Java 21 and Maven 3.9+ are required. Docker Compose starts PostgreSQL, Redis, and Ollama. Pull `llama3.2` first. The application uses PostgreSQL port 5432, Redis 6379 and Ollama 11434. Set `REVIEW_PASSWORD` for a non-demo password. Run `mvn clean test` then `mvn spring-boot:run`. Open Swagger. To avoid Ollama during deterministic local tests use `AGENTIC_MODE=demo`.
