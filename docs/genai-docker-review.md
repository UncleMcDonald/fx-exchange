# GenAI Docker review

Took: run `fx-app-spring` as a non-root `USER`, because the app listens on 8080 and only needs to read `app.jar`, so the security gain is low-risk.

Rejected: pinning base images by digest for now. It improves reproducibility, but it adds a maintenance burden because the digest must be reviewed and bumped deliberately.

Startup-race answer: the correct fix is the Task 2 pattern already in this stack, a MySQL healthcheck plus `depends_on: condition: service_healthy`; plain `depends_on`, sleeps, or `restart: always` do not prove the database is ready.
