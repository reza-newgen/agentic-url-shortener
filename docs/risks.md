# Risks and trade-offs

This intentionally limits AI autonomy: model-generated text is stored for human review rather than automatically applying Java patches. The engine uses synchronized operations per JVM, which do not provide cross-instance locking. On model failures, deterministic fallback permits demonstration but is not equivalent to validated AI reasoning. Cache failure is tolerated on redirect; PostgreSQL remains source of truth. Review credentials must be rotated and TLS enforced in production. Avoid sending sensitive input to untrusted models.
