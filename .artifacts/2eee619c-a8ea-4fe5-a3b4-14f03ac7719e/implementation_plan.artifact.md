# Resolve `:TeamCode:packageDebug` Build Failure

The build is failing during the packaging phase due to an `IncrementalSplitterRunnable` error. This typically indicates a conflict in packaged files (like native libraries, resources, or assets) that the Android Gradle Plugin's incremental packager cannot resolve, or an incompatibility between the Gradle version (9.1.0) and the Android Gradle Plugin version (8.13.2).

## User Review Required

> [!IMPORTANT]
> This plan involves upgrading the Android Gradle Plugin (AGP) to a version compatible with Gradle 9.1.0 and modernizing the packaging configuration.

## Proposed Changes

### Build Configuration

Modernize the packaging DSL and align Gradle/AGP versions.

#### [MODIFY] [build.gradle](file:///Users/brandon/StudioProjects/BioBuzz-2026/build.gradle)
Update the Android Gradle Plugin version to `9.0.0` or higher to ensure compatibility with Gradle 9.1.0.

#### [MODIFY] [TeamCode/build.gradle](file:///Users/brandon/StudioProjects/BioBuzz-2026/TeamCode/build.gradle)
- Update `packagingOptions` to the modern `packaging` block.
- Refine native library packaging to avoid conflicts.

#### [MODIFY] [build.common.gradle](file:///Users/brandon/StudioProjects/BioBuzz-2026/build.common.gradle)
- Update `packagingOptions` to `packaging`.
- Add common exclusions or merges for files known to cause conflicts in FTC projects (e.g., `META-INF` files from dependencies like Marrow or EJML).

### Project Properties

#### [MODIFY] [gradle.properties](file:///Users/brandon/StudioProjects/BioBuzz-2026/gradle.properties)
- Add `android.enableLegacyVariantApi=true` as a temporary compatibility flag if necessary (recommended for AGP 9 migrations).

## Verification Plan

### Automated Tests
- Run `:TeamCode:assembleDebug` to verify the packaging task completes successfully.
- Run a full clean build: `./gradlew clean :TeamCode:assembleDebug`.

### Manual Verification
- Deploy the app to a robot controller or emulator to ensure native libraries are correctly loaded (checking for `UnsatisfiedLinkError`).
