# Moira for macOS Apple Silicon

Signed, notarized, Apple Silicon desktop distribution for the classic Moira Java/SWT workstation. The latest release also includes `Moira-jre.exe` so Windows users can find a ready-made installer in the same place.

[Latest Release](https://github.com/Horace-Maxwell/Moira_APP_MacOS_ARM/releases/latest) |
[中文说明](./README.zh-CN.md) |
[Project Home](./README.md)

## Overview

This repository publishes a modern Apple Silicon distribution of Moira with:

- a bundled arm64 Java runtime for end users
- Java 11 compatible source builds for developers
- a reproducible local build and packaging flow
- a signed and notarized macOS app release

![Moira on macOS Apple Silicon](./docs/assets/moira-app.png)

The goal is straightforward: make the historical Moira desktop codebase usable and maintainable on current Apple Silicon Macs without requiring legacy IDE state or ad-hoc machine setup.

## Why This Fork Exists

The preserved source tree was already made practical for Apple Silicon source-level development by [tutorial0/moira_macOS](https://github.com/tutorial0/moira_macOS). That work proved that the codebase could be opened and debugged on M-series Macs with IntelliJ IDEA and JDK 11.

This repository continues that effort and focuses on:

- release engineering for macOS `.app` and `.dmg`
- runtime hardening for signed app bundles
- stability fixes for SWT drawing on modern macOS
- HiDPI rendering improvements for Retina displays
- cleaner UI behavior in the right-side entry panel
- GitHub Releases delivery for one-click installation

## Major Improvements In This Repository

### 1. Aspect marker rendering is restored safely

The old SWT XOR immediate drawing path could crash on macOS. Marker rendering has been moved to a paint-driven overlay path, so the aspect overlay feature works again without relying on the crash-prone drawing mode.

### 2. HiDPI rendering for high-resolution UI mode

Chart rendering now follows the screen scaling path used by SWT on Retina displays instead of relying on a low-resolution cached bitmap that macOS upscaled.

### 3. Right-side control panel readability

The floating input panel for date, time, and location has been reworked to use an opaque dark background with readable foreground contrast, and the chart drawing area reserves room for it in high-resolution UI mode.

### 4. App bundle safe read/write behavior

Runtime resources and writable user data are now separated:

- read-only bundled resources stay inside the app or source tree
- writable runtime data goes to `~/Library/Application Support/Moira`

This prevents the signed app from attempting to write back into its own bundle.

### 5. Repeatable developer and release scripts

The repository includes:

- `scripts/build-dev.sh` for source builds and direct launch
- `scripts/package-macos.sh` for `.app` and `.dmg` packaging
- `scripts/make-icns.sh` for converting the provided `moira.ico` into a macOS app icon

## Compatibility

- CPU: Apple Silicon (`arm64`)
- Source build target: Java 11
- Release build: bundled runtime inside `Moira.app`
- UI stack: SWT desktop application

## Install For End Users

### macOS

1. Open [Latest Release](https://github.com/Horace-Maxwell/Moira_APP_MacOS_ARM/releases/latest).
2. Download `Moira-*.dmg`.
3. Open the disk image and move `Moira.app` into `Applications`.
4. Launch `Moira.app`.

If you prefer a direct app archive instead of a disk image, the release also publishes a zipped `.app` bundle.

### Windows

1. Open [Latest Release](https://github.com/Horace-Maxwell/Moira_APP_MacOS_ARM/releases/latest).
2. Download `Moira-jre.exe`.
3. Run the installer on Windows.

The release page therefore carries macOS `.dmg` and `.app.zip` assets alongside the Windows installer, making the download options easier to spot.

## Build From Source

Requirements:

- macOS on Apple Silicon
- JDK 11 or newer installed locally

Build:

```bash
./scripts/build-dev.sh
```

Build and run:

```bash
./scripts/build-dev.sh --run
```

The staged development launcher is:

```text
build/dev/Moira.sh
```

## Create The macOS App Bundle

Unsigned local packaging:

```bash
./scripts/package-macos.sh
```

Outputs:

- `dist/Moira.app`
- `dist/Moira-1.50.2.dmg`

## Sign And Notarize

With a keychain notary profile:

```bash
export MOIRA_SIGN_IDENTITY="Developer ID Application: Your Name (TEAMID)"
export MOIRA_NOTARY_PROFILE="your-notary-profile"
./scripts/package-macos.sh
```

With Apple ID credentials:

```bash
export MOIRA_SIGN_IDENTITY="Developer ID Application: Your Name (TEAMID)"
export MOIRA_APPLE_ID="your@appleid.com"
export MOIRA_APPLE_APP_PASSWORD="xxxx-xxxx-xxxx-xxxx"
export MOIRA_APPLE_TEAM_ID="TEAMID"
./scripts/package-macos.sh
```

Optional variables:

- `MOIRA_BUNDLE_ID`
- `MOIRA_ICON_SOURCE`
- `MOIRA_SIGNING_KEYCHAIN`

## Runtime Data Model

Install root resolution order:

1. explicit install path from launcher
2. `Contents/app` inside the packaged `.app`
3. code source location
4. current working directory fallback

Writable user data:

```text
~/Library/Application Support/Moira
```

Ephemeris downloads and mutable runtime files are redirected there.

## Repository Layout

- `src/` application sources
- `lib/` bundled library jars
- `ephe/` bundled ephemeris resources
- `icon/` legacy icon resources
- `scripts/` local build, icon, and package tooling

## Credits

- Original Moira desktop application by At Home Projects
- Apple Silicon source-tree preservation and IntelliJ-friendly setup by [tutorial0/moira_macOS](https://github.com/tutorial0/moira_macOS)

This repository explicitly acknowledges the contribution of `tutorial0/moira_macOS`, which kept the codebase practical on Apple Silicon and provided the base context for continued maintenance.

## License

The repository keeps a single main root license entry in [`LICENSE`](./LICENSE), preserving the historical Moira GPL text. Third-party notices are separated into [docs/licenses/LGPL-2.1.txt](./docs/licenses/LGPL-2.1.txt) and [docs/licenses/Swiss-Ephemeris-SEPL-0.2.txt](./docs/licenses/Swiss-Ephemeris-SEPL-0.2.txt).

## Status

This repository is intended to be a stable Apple Silicon desktop distribution and source maintenance base for Moira on modern macOS.
