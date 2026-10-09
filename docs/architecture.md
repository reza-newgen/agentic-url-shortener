# Architecture

The URL Shortener and Agentic SDLC workflow coexist in one Spring Boot application, using `com.schwab.agentic` as the package root. REST requests start persistent `WorkflowRun` and `WorkflowTask` records. `DependencyGraph` produces dependency levels. `WorkflowEngine` executes independent testing/security/documentation agents on virtual threads and joins their futures. Human gates before architecture, implementation and release review use manual approval APIs. State and audit events persist in PostgreSQL. Spring AI ChatClient uses Ollama with a deterministic degraded response if unavailable.

Workflow: analysis → planning → approval → architecture → approval → implementation → [testing || security || documentation] → approval → review → final release authorization. Reviewable artifacts are persisted text; no untrusted AI source is automatically compiled, merged, or deployed.
