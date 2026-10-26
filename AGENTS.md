# Repository Guidelines

## Project Structure & Module Organization

This is a single-module Android application built with Kotlin and Jetpack Compose.

- `app/src/main/java/br/com/V1nic1us/callblocker/`: application code, including `MainActivity`, the call-screening service, filtering policy, contact lookup, preferences, navigation, and UI.
- `app/src/main/res/`: Android manifest, strings, themes, icons, and other resources.
- `app/src/test/`: JVM unit tests for policy and non-Android logic.
- `app/src/androidTest/`: emulator/device tests for Compose UI, preferences, contacts, and permissions.
- `docs/superpowers/`: design specifications and implementation plans.

## Build, Test, and Development Commands

Run commands from the repository root:

```bash
./gradlew test                         # JVM unit tests
./gradlew connectedDebugAndroidTest    # Instrumented tests on a connected device/emulator
./gradlew assembleDebug                # Build the debug APK
./gradlew test assembleDebug            # Common local verification
```

The app targets Android SDK 36, has a minimum SDK of 29, and uses Java/Kotlin toolchain 17. A connected emulator is required for `connectedDebugAndroidTest`.

## Coding Style & Naming Conventions

Use idiomatic Kotlin with four-space indentation. Classes and composables use `PascalCase`; functions, properties, and test names use `camelCase`. Keep Android integration at service/activity boundaries and deterministic filtering rules in testable pure classes. Follow existing Compose/Material 3 patterns and keep dependencies in `gradle/libs.versions.toml`.

## Design System

The UI uses Jetpack Compose with Material 3. Reuse `BloqueadorDeChamadasTheme` and the shared tokens in `app/src/main/java/br/com/V1nic1us/callblocker/theme/` instead of hard-coded visual values. Keep colors in `Color.kt`, typography in `Type.kt`, shapes in `Shape.kt`, and spacing in `Spacing.kt`. Prefer `MaterialTheme.colorScheme`, `MaterialTheme.typography`, `MaterialTheme.shapes`, and `AppSpacing` in composables. New components should follow the existing Material 3 cards, switches, radio buttons, surface colors, and accessibility semantics.

## Testing Guidelines

Use JUnit tests under `app/src/test` for platform-independent behavior. Use `app/src/androidTest` when testing Android providers, runtime permissions, Compose UI, or Telecom integration. Name tests for the behavior they protect, such as ``allows known contacts when filtering is enabled``. Add a regression test before changing filtering behavior.

## Commit & Pull Request Guidelines

Use short, imperative commit subjects describing one change, for example `Allow known contacts through call screening`. Keep commits focused. Pull requests should target `dev`, explain the user-visible behavior, list verification commands and emulator/device coverage, and call out any unverified physical-device or live-call behavior. Include screenshots when changing the Compose UI.

## Security & Configuration Tips

Contact data and call-filter preferences are local app data. Do not log phone numbers or commit secrets. Changes involving `READ_CONTACTS`, Telecom screening, or backup rules require tests and documented permission/fallback behavior.

## Codex Workflow

Before editing, inspect relevant files, the branch, and existing tests. Keep changes scoped and preserve unrelated work. Use a feature/fix branch, target `dev` for PRs, and never rewrite shared history without approval. For behavior changes, add a regression test first, confirm it fails, then implement the smallest fix. Before completion, run unit tests, instrumented tests when a device is available, and `assembleDebug`; report unavailable checks honestly.
