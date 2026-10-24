# Task 1 report: call screening policy

## Status

Completed. The policy now accepts a known-contact flag with a default of `false`. When filtering is enabled and the call otherwise qualifies for screening, known contacts are allowed before reject/silence mode is applied. Unknown callers retain the existing reject and silence behavior.

## Files

- `app/src/main/java/br/com/V1nic1us/callblocker/CallScreeningPolicy.kt`
- `app/src/test/java/br/com/V1nic1us/callblocker/CallScreeningPolicyTest.kt`

The RED tests added before implementation were preserved. They verify that a known contact is allowed and omission of contact data retains the default unknown-caller behavior.

## TDD and verification

1. RED: `./gradlew :app:testDebugUnitTest` failed while compiling the new test because `evaluate` did not yet accept `isKnownContact` (`No parameter with name 'isKnownContact' found`). This confirmed the expected missing API.
2. GREEN: added `isKnownContact: Boolean = false` and the ALLOW short-circuit after existing filtering eligibility checks.
3. `./gradlew :app:testDebugUnitTest` — **BUILD SUCCESSFUL**.
4. `./gradlew test` — **BUILD SUCCESSFUL**.

## Concerns

No known concerns for this policy change. The contact lookup/integration caller remains outside Task 1; callers that do not supply contact data continue to treat calls as unknown through the default value.
