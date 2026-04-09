<div align="center">

# Moira for macOS Apple Silicon

Signed, notarized, Apple Silicon desktop distribution for the classic Moira Java/SWT workstation

简体中文 | [English](./README.en.md)

[![release](https://img.shields.io/github/v/release/Horace-Maxwell/Moira_APP_MacOS_ARM?display_name=release&label=release)](https://github.com/Horace-Maxwell/Moira_APP_MacOS_ARM/releases)
[![platform](https://img.shields.io/badge/platform-macOS%20Apple%20Silicon-111111)](https://github.com/Horace-Maxwell/Moira_APP_MacOS_ARM)
[![runtime](https://img.shields.io/badge/runtime-bundled%20arm64%20JRE-0A84FF)](https://github.com/Horace-Maxwell/Moira_APP_MacOS_ARM/releases)
[![source](https://img.shields.io/badge/source%20compat-Java%2011-F59E0B)](https://github.com/Horace-Maxwell/Moira_APP_MacOS_ARM)
[![distribution](https://img.shields.io/badge/distribution-Developer%20ID%20%2B%20Notarized-16A34A)](https://github.com/Horace-Maxwell/Moira_APP_MacOS_ARM/releases)
[![license](https://img.shields.io/github/license/Horace-Maxwell/Moira_APP_MacOS_ARM)](./LICENSE)

[Latest Release](https://github.com/Horace-Maxwell/Moira_APP_MacOS_ARM/releases/latest) |
[Releases](https://github.com/Horace-Maxwell/Moira_APP_MacOS_ARM/releases) |
[中文说明](./README.zh-CN.md) |
[English Guide](./README.en.md) |
[Contributing](./CONTRIBUTING.md) |
[Security](./SECURITY.md)

</div>

This repository publishes a maintained Apple Silicon port of Moira with a ready-to-install macOS app bundle, bundled arm64 runtime, Java 11 source compatibility, and a reproducible local packaging workflow.

![Moira on macOS Apple Silicon](./docs/assets/moira-app.png)

## Start Here / 先看这里

| Audience | What to do |
| --- | --- |
| End users | Download the latest signed installer from [GitHub Releases](https://github.com/Horace-Maxwell/Moira_APP_MacOS_ARM/releases/latest). Open the `.dmg`, drag `Moira.app` into `Applications`, then launch it like a normal macOS app. |
| Developers | Read the full setup guides: [English](./README.en.md) or [简体中文](./README.zh-CN.md). Source builds remain Java 11 compatible and can be launched directly from the repository. |

## Highlights / 核心特性

- Signed and notarized `Moira.app` and `.dmg` for Apple Silicon Macs.
- Bundled arm64 Java runtime for end users, without requiring a local JDK.
- Restored aspect marker overlays without using the SWT XOR drawing path that crashes on modern macOS builds.
- HiDPI chart rendering path for Retina displays in high-resolution UI mode.
- Opaque, readable right-side entry panel for date, time, and location controls.
- Writable runtime data model based on `~/Library/Application Support/Moira`, so the signed app does not try to mutate its own bundle.
- Reproducible source build and packaging scripts for local development, signing, notarization, and release generation.

## What This Repository Adds / 这个仓库额外做了什么

- Converts the Apple Silicon compatibility work into a real desktop release flow.
- Preserves Java 11 source compatibility while producing a self-contained macOS app.
- Packages source, resources, icons, runtime, signing, notarization, and release assets in one place.
- Documents the runtime path model, build process, release workflow, and platform constraints in both Chinese and English.

## Project Lineage / 项目来源

- Original desktop application: Moira by At Home Projects.
- Apple Silicon IDEA-oriented reference port: [tutorial0/moira_macOS](https://github.com/tutorial0/moira_macOS).
- This repository builds on that groundwork and adds desktop release engineering, runtime packaging, platform fixes, UI cleanup, HiDPI rendering, GitHub Releases distribution, and repeatable notarized delivery.

Special thanks to the maintainer of `tutorial0/moira_macOS` for preserving a practical Apple Silicon source tree, resolving the original SWT dependency setup for IntelliJ IDEA, and documenting the first workable M-series development path.

## Technical Scope / 技术范围

- Target platform: macOS on Apple Silicon (`arm64`)
- Source compatibility target: Java 11
- Desktop UI stack: Java + SWT
- Release format: signed `Moira.app`, notarized `.dmg`, optional zipped `.app` for direct download
- User-writable data directory: `~/Library/Application Support/Moira`

## License / 许可证

This repository keeps the historical Moira licensing model and ships the upstream GPL text in [`LICENSE`](./LICENSE). Additional third-party license texts included in the source tree remain preserved as-is.

## Notes / 说明

- This is an engineering and packaging repository. It is focused on runtime stability, Apple Silicon compatibility, and desktop delivery quality.
- It is not an official At Home Projects release.
- For full build, packaging, and runtime notes, continue to [README.en.md](./README.en.md) or [README.zh-CN.md](./README.zh-CN.md).
