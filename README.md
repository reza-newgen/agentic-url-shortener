# Agentic URL Shortener

Java 21, Spring Boot 4.1.1, Spring AI 2.0.1, Ollama, PostgreSQL, Redis, Spring Security, Flyway, REST, OpenAPI, Actuator, Micrometer, JUnit, Mockito and Testcontainers.

## Quick start

```bash
docker compose up -d db redis ollama
docker compose exec ollama ollama pull llama3.2
mvn clean test
mvn spring-boot:run
```

Or `docker compose up --build -d` after pulling the model.

Login: `reviewer` / value of `REVIEW_PASSWORD` (dev default `change-me-before-deploy`). **Set a strong password for any shared deployment.**

## URL API

```bash
curl -u reviewer:change-me-before-deploy -H 'Content-Type: application/json' -d '{"url":"https://example.com"}' http://localhost:8080/api/urls
curl -i http://localhost:8080/r/SHORTCODE
curl -u reviewer:change-me-before-deploy http://localhost:8080/api/urls/SHORTCODE/analytics
```

## Agentic workflow API — automatic execution

Submit **once**. A background worker runs eligible stages, including parallel testing/security/docs, until the next approval gate; you **do not call `/advance`**.

```bash
curl -u reviewer:change-me-before-deploy -H 'Content-Type: application/json' \
  -d '{"scenario":"greenfield","requirement":"Add URL expiration and tests"}' \
  http://localhost:8080/api/workflows
# Use the ID returned above. Poll status and outputs at any time:
curl -u reviewer:change-me-before-deploy http://localhost:8080/api/workflows/RUN_ID
curl -u reviewer:change-me-before-deploy http://localhost:8080/api/workflows/RUN_ID/tasks
curl -u reviewer:change-me-before-deploy http://localhost:8080/api/workflows/RUN_ID/audit
# When the run says AWAITING_APPROVAL, inspect the tasks and approve the pending step:
curl -u reviewer:change-me-before-deploy -X POST http://localhost:8080/api/workflows/RUN_ID/approve/architecture
# Later: approve/implementation, and approve/review. Each approval auto-resumes the workflow.
# Once READY_FOR_RELEASE, the reviewer can explicitly authorize release:
curl -u reviewer:change-me-before-deploy -X POST http://localhost:8080/api/workflows/RUN_ID/release
```

Approval gates: **architecture**, **implementation**, **review**. These are intentional human-control boundaries. The `/advance` HTTP endpoint has been removed; all other eligible transitions happen automatically. Replanning also starts a new automated pass. `/stop` and `/rollback` remain available.

The POST response can show `CREATED` because work starts asynchronously immediately after it is saved. Poll GET for updates. A `DONE` testing/security task means the agent has produced a review artifact; it does **not** mean JUnit tests or vulnerability scans actually ran.

Set `AGENTIC_MODE=demo` to run with deterministic responses and without making model calls; default mode calls Ollama through Spring AI and falls back to deterministic text if the model is unavailable.

Swagger: http://localhost:8080/swagger-ui/index.html — Metrics: http://localhost:8080/actuator/prometheus

**Honest scope:** This educational working prototype orchestrates AI-generated review artifacts, not automated repository mutations or production deployment. See docs/engineering-summary.md for capability/limitation details.
