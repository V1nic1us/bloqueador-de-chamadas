# Material 3 Design System Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Complete an adaptive Material 3 theme and apply it consistently to the call filter settings screen.

**Architecture:** Keep semantic color roles, typography, shape, and spacing tokens in the existing theme package. Keep settings state and call screening behavior in `MainActivity`; use Material 3 surfaces and controls to present the existing settings.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Android ADB.

**Spec:** `docs/superpowers/specs/2026-10-23-material3-design-system-design.md`

## Global Constraints

- Keep dynamic color enabled on Android 12+.
- Use the app's fallback light and dark schemes when dynamic color is unavailable or disabled.
- Preserve current Portuguese copy, settings persistence, call filtering behavior, and accessibility semantics.
- Add no external fonts or UI libraries.

## Review Focus

- Light and dark modes retain readable foreground/surface contrast; manually inspect both emulator modes.
- Dynamic color and fallback schemes both render correctly; verify dynamic on emulator and fallback through the theme configuration.
- Disabled mode choices remain visibly disabled and retain radio semantics; inspect filter off/on states.
- Larger system font scaling preserves wrapping and scrolling; inspect at enlarged font scale.
- Role unavailable, available, and held messages use the same semantic status treatment; inspect available and use existing state branches for the others.

---

### Task 1: Define Material 3 theme tokens

**Files:**
- Modify: `app/src/main/java/com/example/bloqueadordechamadas/theme/Color.kt`
- Modify: `app/src/main/java/com/example/bloqueadordechamadas/theme/Theme.kt`
- Modify: `app/src/main/java/com/example/bloqueadordechamadas/theme/Type.kt`
- Create: `app/src/main/java/com/example/bloqueadordechamadas/theme/Shape.kt`
- Create: `app/src/main/java/com/example/bloqueadordechamadas/theme/Spacing.kt`

**Interfaces:**
- Produce light/dark semantic Material 3 color schemes, `Typography`, `Shapes`, and a shared spacing scale for later UI use.
- Keep `BloqueadorDeChamadasTheme(darkTheme, dynamicColor, content)` as the public theme entry point.

- [x] Replace starter purple palette with a blue/indigo app fallback palette and semantic light/dark roles.
- [x] Complete Material 3 typography and define shared `Shapes` and spacing tokens.
- [x] Preserve dynamic color selection on API 31+ and fallback selection otherwise.
- [x] Build `:app:assembleDebug`; expect successful Kotlin/resource compilation.

### Task 2: Apply the system to call filter settings

**Files:**
- Modify: `app/src/main/java/com/example/bloqueadordechamadas/MainActivity.kt`

**Interfaces:**
- Consume the theme tokens from Task 1.
- Preserve `MainActivity` preference callbacks, role request flow, existing labels, switch/radio semantics, and scroll behavior.

- [x] Group the filter control, treatment choices, and Android screening role status into Material 3 surfaces with consistent spacing and hierarchy.
- [x] Use semantic theme colors and shapes for selected, disabled, and status treatments.
- [x] Build and install the debug app on the existing emulator, then inspect light, dark, filter off/on, and enlarged-text screens through ADB.
- [x] Confirm no user-facing text or preference behavior changed during the visual update.
