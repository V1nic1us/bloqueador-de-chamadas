# Task 1 report: Android app scaffold

Generated the project in the worktree root with the Android CLI `empty-activity` template, app name `Bloqueador de Chamadas`, and minSdk 29. The initial prescribed command was rejected because the worktree already contained documentation; rerunning with `--force` succeeded. The only pre-existing root-level file the CLI reported overwriting was `.gitignore`; its original `.worktrees/` exclusion has been restored alongside the Android template ignore rules.

## Generated interfaces

- Package and Gradle namespace: `com.example.bloqueadordechamadas`
- Application ID: `com.example.bloqueadordechamadas`
- App module: `app`
- Compose activity: `app/src/main/java/com/example/bloqueadordechamadas/MainActivity.kt`
- Manifest: `app/src/main/AndroidManifest.xml`
- Root/module Gradle files, version catalog and wrapper: `build.gradle.kts`, `app/build.gradle.kts`, `gradle/libs.versions.toml`, `gradle/wrapper/`, `gradlew`, and `gradlew.bat`
- Minimum SDK in `app/build.gradle.kts`: 29

The template also produced its default Compose navigation, resources, and local/instrumented test source files. No tests were added by hand or run.

## Review

Inspected the app Gradle configuration, manifest, activity source, settings, generated file list, and git changes. The package, application ID, minSdk and activity path agree across the scaffold. No build or test execution was performed.

## Review fix round

Changed `app/src/main/AndroidManifest.xml` to set `android:allowBackup="false"`, keeping future app preferences on-device. Removed the generated placeholder local and instrumented test source files; template test dependencies remain in the Gradle configuration because this round avoids unrelated dependency changes.

Checks performed: inspected the updated manifest, confirmed the placeholder source files are absent, and reviewed the Git diff with whitespace checks. Tests were not run because they were not requested.
