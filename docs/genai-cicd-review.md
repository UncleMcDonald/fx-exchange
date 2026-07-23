# GenAI CI/CD Review

## Adopted

I adopted CD concurrency:

```yaml
concurrency:
  group: cd-main-staging
  cancel-in-progress: false
```

This is worth the cost for a capstone because two pushes to `main` should not race each other through the same staging gate. The cost is acceptable: a newer CD run may wait behind an older one instead of running immediately.

## Rejected

I rejected adding a Java version matrix such as `21, 22`. The project ships Java 21, so a matrix would double CI time and dependency downloads to test a runtime we do not deploy.

I also rejected splitting unit, slice, and integration tests into separate CI jobs. The graph would look cleaner, but Testcontainers and Maven setup would be repeated across jobs, and the full `verify` signal would be easier to misread.

## Faster CI Advice Check

The dangerous "make CI faster" advice is to replace `mvn verify` with `mvn test`, skip ITs, or demote `RateRepositoryIT` to a less frequent job. That sacrifices the only test that proves the real SQL works against a real MySQL.

The way to catch that mistake is the Task 3 honesty check: a green `verify` can still hide `RateRepositoryIT` with `Skipped: 3`. Any speed advice that makes the integration tier optional must explain how reviewers will still prove `Skipped: 0`; otherwise it is faster because it is blind.
