# Bloqueador de chamadas fora dos contatos Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build an Android app that filters calls outside the user's contacts, with a setting to reject them or silence their ringtone.

**Architecture:** A Kotlin Android app uses a small Compose screen, locally persisted preferences, and a `CallScreeningService` registered in the manifest. The screen checks and requests the Android call screening role; the service reads the current preferences and responds to incoming calls.

**Tech Stack:** Kotlin, Android SDK 29+, Jetpack Compose from the Android CLI `empty-activity` template, Android Telecom APIs, and SharedPreferences.

**Spec:** `docs/superpowers/specs/2026-10-23-bloqueador-de-chamadas-design.md`

## Global Constraints

- Minimum Android version is Android 10 (API 29).
- Keep all preferences local; do not add a backend or transmit call data.
- Filter calls outside contacts only; don't read or copy the contacts list.
- The user must grant the app the Android call screening role.
- With filtering disabled, pass calls without silencing or rejecting them.
- Do not suppress call log entries or missed call notifications.

## Review Focus

- App is not the selected call screening service: show the role state and an action to request it.
- Filtering is disabled: allow the call with no reject or silence flags.
- Preference storage is missing or malformed: default safely to filtering disabled and silence mode.
- Outgoing calls: allow without applying the incoming call filter.
- Unsupported or missing phone handle: allow without treating the call as an ordinary unknown number.

---

### Task 1: Scaffold the Android app

**Files:**
- Create: Android project files from `android create empty-activity --name="Bloqueador de Chamadas" --output=. --minSdk=29`

**Interfaces:**
- Produces the package name, app module, Compose activity, manifest, and Gradle wrapper used by later tasks.

- [ ] Generate the Android project from the official CLI's `empty-activity` template with minSdk 29.
- [ ] Confirm the generated package, application ID, and Compose activity paths; use them consistently in later tasks.

### Task 2: Add local screening preferences and call service

**Files:**
- Create: `app/src/main/java/<generated-package>/CallFilterPreferences.kt`
- Create: `app/src/main/java/<generated-package>/CallScreeningService.kt`
- Modify: `app/src/main/AndroidManifest.xml`

**Interfaces:**
- `CallFilterPreferences(context: Context)` exposes `isEnabled(): Boolean`, `mode(): Mode`, `setEnabled(enabled: Boolean)`, and `setMode(mode: Mode)`; `Mode` has `REJECT` and `SILENCE` values.
- `CallScreeningService` reads these preferences for each incoming call and responds through `respondToCall`.

- [ ] Implement SharedPreferences-backed settings with filtering disabled and `SILENCE` as safe defaults; malformed mode values also resolve to `SILENCE`.
- [ ] Register the screening service with `android.permission.BIND_SCREENING_SERVICE` and the `android.telecom.CallScreeningService` intent action.
- [ ] Implement call handling: pass outgoing calls and calls with no usable telephone handle; when disabled, pass through; when enabled, reject using `setDisallowCall(true)` and `setRejectCall(true)` for `REJECT`, or set `setSilenceCall(true)` for `SILENCE`.
- [ ] Keep call log and missed call notifications at platform defaults.

### Task 3: Build the settings screen and role flow

**Files:**
- Modify: `app/src/main/java/<generated-package>/MainActivity.kt`
- Modify: generated Compose theme files only if needed for readable Portuguese UI.

**Interfaces:**
- Activity owns the screen state and requests the `RoleManager.ROLE_CALL_SCREENING` role through Android's role request intent.
- UI edits preference values through `CallFilterPreferences`.

- [ ] Show a Portuguese title, a clear on/off switch, and a two-choice selector for automatic rejection or silent ringing.
- [ ] Disable mode selection while filtering is off and persist changes immediately.
- [ ] Show whether call screening is enabled for this app and provide a button that launches the system role request flow when it is not.
- [ ] Refresh the displayed role state when the activity resumes, since the user may complete or cancel the system flow.

### Task 4: Package and verify the app

**Files:**
- Modify generated project files only if required to resolve build configuration issues.

**Interfaces:**
- Produces the debug APK at the Gradle build output path.

- [ ] Build the debug APK with `./gradlew assembleDebug`.
- [ ] Inspect the generated APK path and report the Android device requirement: Android 10+ and user-granted call screening role.

