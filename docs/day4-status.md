# Day 4 Status

## Test Altitudes

- Unit: `com.fx.api.ConversionServiceTest` checks conversion rounding, 1% fee, net amount, and unknown-pair failure without Spring or a database.
- Slice: `com.fx.api.web.RateControllerTest` checks `GET /api/rates`, unknown pair `404`, valid conversion `201`, and negative amount `400` with mocked collaborators.
- Integration: `com.fx.api.repo.RateRepositoryIT` uses Testcontainers MySQL and Liquibase-owned schema/data to check `findLatest()` returns 10 rows, EUR/USD is 1.0818, and an unknown pair is empty.

## CI Guardrails

- `build` runs `mvn -B verify` from `fx-app-spring`, so PRs exercise unit, slice, and Testcontainers integration tests on GitHub's Docker-enabled runner.
- `lint` runs `mvn -B validate` as a fast parallel Maven sanity check.
- `dependency-check` runs OWASP dependency-check and is `continue-on-error: true`, so findings surface without blocking every student PR.
- Branch protection requires the `build` check before `main` can be merged.

## CI Gaps

- CI does not do a full end-to-end compose smoke test that starts the API, MySQL, monitor, and orchestrator, then curls the app as a user.
- CI does not prove GitHub branch protection unless the repository settings require `build`.
- CI can still show `BUILD SUCCESS` when Testcontainers is intentionally skipped because Docker is unavailable; the failsafe summary must be read.
- The front end is not covered yet; `fx-dashboard` is Week 3 scope.

## Supply Chain

- `mvn dependency:tree` reported 93 dependency entries for `fx-app-spring`, including direct and transitive dependencies.
- Transitive trace: `org.apache.tomcat.embed:tomcat-embed-el:10.1.30` is pulled in by `spring-boot-starter-validation`.
- Transitive trace: `com.github.docker-java:docker-java-api:3.4.2` is pulled in by `org.testcontainers:testcontainers:1.21.4`.
- Manual advisory lookup: `org.apache.commons:commons-text:1.11.0` is pulled in by Liquibase. CVE-2022-42889 affects Commons Text 1.5 through 1.9 and is fixed by 1.10.0, so 1.11.0 is not affected by that advisory.
- Dependabot rule: Dependabot PRs are reviewed within one working day; security PRs are merged the same day when CI is green. The committed `dependabot.yml` covers Maven and GitHub Actions updates, and GitHub Dependabot alerts plus security updates are enabled for the repository.

## Honesty Checks

- MySQL off / fast tier: `mvnw.cmd test` passed with 29 tests, 0 failures, 1 pre-existing skip, showing unit and slice tests do not secretly require a local database.
- Docker unavailable / full tier: `mvnw.cmd verify` reached failsafe with Testcontainers 1.21.4, then reported `RateRepositoryIT` as 3 skipped tests and still ended `BUILD SUCCESS`. That green result is not enough unless the IT summary says `Skipped: 0`.

## Definition of Done

A change is done only when all of these hold:

1. From `fx-app-spring/`, `mvnw.cmd verify` is green across unit, slice, and integration tests, with nothing wrongly skipped, and `docker compose up` on a clean machine serves `/api/rates` returning 10 rates.
2. The new behavior has tests at the right altitude: a new endpoint gets slice tests for happy and failure paths, new calculation logic gets unit tests with boundaries, and new or changed SQL gets an `*IT`.
3. A teammate reviewed the PR, checked out the branch, ran `mvnw.cmd verify`, and approved.
4. Changes merge to `main` only through a PR, never by direct push.
5. `main` is still green after merge, and a fresh clone builds and verifies.

Not done: a failing or wrongly skipped test; "works on my laptop" without a fresh-clone check; code merged without review.
