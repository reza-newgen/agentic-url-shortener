# Automatic workflow execution

- `POST /api/workflows` persists a run and schedules it on a background executor.
- The driver advances eligible dependency-graph levels without client intervention.
- Parallel testing/security/documentation work remains inside the existing graph execution.
- Architecture, implementation and review require an explicit `POST /approve/{step}`. Approval requeues the run automatically.
- The reviewer must explicitly invoke `/release` after `READY_FOR_RELEASE`.
- `POST /replan` resets stage artifacts and automatically requeues analysis.
- `POST /stop` and `/rollback` are manual intervention endpoints.
- On restart, incomplete CREATED/RUNNING workflows are resumed; WAITING_APPROVAL and terminal workflows are left alone.
- Repeated triggers for the same workflow are coalesced. Driver exceptions mark the run FAILED.

## Boundaries

This changes workflow triggering, **not** the maturity of generated artifacts. Agents produce reviewable text, and do not apply patches, execute generated tests, or deploy to production. A running task can finish before a stop request acquires the engine lock. The executor is in-process; for horizontally scaled deployment, use DB leases/queue-based scheduling.
