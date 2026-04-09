# Contributing

Thanks for helping improve the Apple Silicon desktop distribution of Moira.

## Scope

Useful contributions include:

- macOS Apple Silicon runtime fixes
- SWT stability work
- packaging and notarization improvements
- UI clarity and accessibility refinements
- documentation and release workflow improvements

## Local Setup

Requirements:

- macOS on Apple Silicon
- JDK 11 or newer

Build:

```bash
./scripts/build-dev.sh
```

Build and run:

```bash
./scripts/build-dev.sh --run
```

Package:

```bash
./scripts/package-macos.sh
```

## Before Opening A PR

- Keep source compatibility at Java 11 level unless there is a deliberate project-wide change.
- Preserve the runtime data model that writes mutable files to `~/Library/Application Support/Moira`.
- Avoid reintroducing SWT XOR drawing paths that are unstable on macOS.
- Test the packaged app when changing runtime paths, packaging, signing, or UI scaling behavior.
- Do not commit local certificates, signing requests, keychain exports, or other sensitive files.

## Reporting Changes

Please describe:

- what problem you fixed
- how the behavior changed
- how you verified it on Apple Silicon macOS

## Attribution

When touching historical code, preserve upstream notices and keep the main GPL license plus any bundled third-party notices accurate.
