# Required scenarios

* **Greenfield:** `Add a link expiration option, migration, REST behavior and tests` — starts with independent requirement analysis and produces a planned feature proposal and review artifacts.
* **Brownfield:** `Improve redirect analytics under concurrent load without lost counts` — review current JPA update pattern, propose atomic updates, tests and documentation.
* **Ambiguous:** `Make links secure and faster` — require identification of unknown success criteria and reviewer governance before change.

Submit each with the `scenario` field through `/api/workflows`; use `/advance`, `/approve/{step}`, `/tasks` and `/audit` to demonstrate the graph, parallel stages and decisions. Replan using `/replan` with updated requirement text.
