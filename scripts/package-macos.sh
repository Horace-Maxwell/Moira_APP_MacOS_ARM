#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD_SCRIPT="$ROOT_DIR/scripts/build-dev.sh"
ICON_SCRIPT="$ROOT_DIR/scripts/make-icns.sh"
BUILD_APP_DIR="$ROOT_DIR/build/dev/app"
DIST_DIR="$ROOT_DIR/dist"
APP_NAME="Moira"
APP_VERSION="1.50.2"
BUNDLE_ID="${MOIRA_BUNDLE_ID:-com.athomeprojects.moira}"
ICON_SOURCE="${MOIRA_ICON_SOURCE:-$ROOT_DIR/moira.ico}"
ICON_FILE="$ROOT_DIR/build/dev/Moira.icns"

JPACKAGE_BIN="${JAVA_HOME:+$JAVA_HOME/bin/}jpackage"
JDEPS_BIN="${JAVA_HOME:+$JAVA_HOME/bin/}jdeps"

if [[ ! -x "$JPACKAGE_BIN" ]]; then
  JPACKAGE_BIN="$(command -v jpackage)"
fi
if [[ ! -x "$JDEPS_BIN" ]]; then
  JDEPS_BIN="$(command -v jdeps)"
fi

mkdir -p "$DIST_DIR"
"$BUILD_SCRIPT"
if [[ -f "$ICON_SOURCE" ]]; then
  "$ICON_SCRIPT" "$ICON_SOURCE" "$ICON_FILE"
fi

classpath_entries=()
for jar in "$BUILD_APP_DIR"/lib/*.jar; do
  classpath_entries+=("$jar")
done
classpath_string="$(IFS=:; echo "${classpath_entries[*]}")"
module_deps="$("$JDEPS_BIN" --multi-release 11 --ignore-missing-deps \
  --print-module-deps --class-path "$classpath_string" \
  "$BUILD_APP_DIR/Moira.jar" 2>/dev/null || true)"
if [[ -z "$module_deps" ]]; then
  module_deps="java.base,java.desktop,java.logging,java.prefs"
fi

jpackage_common=(
  --name "$APP_NAME"
  --app-version "$APP_VERSION"
  --vendor "At Home Projects"
  --copyright "Copyright 2004-2015 At Home Projects"
  --input "$BUILD_APP_DIR"
  --main-jar "Moira.jar"
  --main-class "org.athomeprojects.moira.Moira"
  --dest "$DIST_DIR"
  --add-modules "$module_deps"
  --java-options "-XstartOnFirstThread"
  --java-options "-Xdock:name=Moira"
  --java-options "-Dapple.awt.application.name=Moira"
  --mac-package-identifier "$BUNDLE_ID"
  --mac-package-name "$APP_NAME"
  --mac-app-category "public.app-category.utilities"
)

if [[ -f "$ICON_FILE" ]]; then
  jpackage_common+=(--icon "$ICON_FILE")
fi

sign_args=()
if [[ -n "${MOIRA_SIGN_IDENTITY:-}" ]]; then
  sign_args+=(--mac-sign --mac-app-image-sign-identity "$MOIRA_SIGN_IDENTITY")
  sign_args+=(--mac-package-signing-prefix "$BUNDLE_ID")
  if [[ -n "${MOIRA_SIGNING_KEYCHAIN:-}" ]]; then
    sign_args+=(--mac-signing-keychain "$MOIRA_SIGNING_KEYCHAIN")
  fi
fi

can_notarize=0
if [[ -n "${MOIRA_NOTARY_PROFILE:-}" ]]; then
  can_notarize=1
elif [[ -n "${MOIRA_APPLE_ID:-}" && -n "${MOIRA_APPLE_APP_PASSWORD:-}" && -n "${MOIRA_APPLE_TEAM_ID:-}" ]]; then
  can_notarize=1
fi

if (( can_notarize )) && [[ -z "${MOIRA_SIGN_IDENTITY:-}" ]]; then
  echo "Notarization requires MOIRA_SIGN_IDENTITY for signing." >&2
  exit 1
fi

submit_for_notarization() {
  local artifact="$1"
  if [[ -n "${MOIRA_NOTARY_PROFILE:-}" ]]; then
    xcrun notarytool submit "$artifact" --keychain-profile "$MOIRA_NOTARY_PROFILE" --wait
  else
    xcrun notarytool submit "$artifact" \
      --apple-id "$MOIRA_APPLE_ID" \
      --password "$MOIRA_APPLE_APP_PASSWORD" \
      --team-id "$MOIRA_APPLE_TEAM_ID" \
      --wait
  fi
}

zip_for_notarization() {
  local app_image="$1"
  local zip_path="$2"
  rm -f "$zip_path"
  ditto -c -k --keepParent "$app_image" "$zip_path"
}

rm -rf "$DIST_DIR/$APP_NAME.app"
jpackage_app=( "$JPACKAGE_BIN" --type app-image "${jpackage_common[@]}" )
if (( ${#sign_args[@]} > 0 )); then
  jpackage_app+=( "${sign_args[@]}" )
fi
"${jpackage_app[@]}"

APP_IMAGE="$DIST_DIR/$APP_NAME.app"
if (( can_notarize )); then
  APP_ZIP="$DIST_DIR/$APP_NAME-notarize.zip"
  zip_for_notarization "$APP_IMAGE" "$APP_ZIP"
  submit_for_notarization "$APP_ZIP"
  xcrun stapler staple "$APP_IMAGE"
  rm -f "$APP_ZIP"
fi

rm -f "$DIST_DIR"/"$APP_NAME"*.dmg
jpackage_dmg=(
  "$JPACKAGE_BIN" --type dmg --name "$APP_NAME" --app-version "$APP_VERSION"
  --dest "$DIST_DIR"
  --app-image "$APP_IMAGE" --license-file "$ROOT_DIR/LICENSE"
)
if (( ${#sign_args[@]} > 0 )); then
  jpackage_dmg+=( "${sign_args[@]}" )
fi
"${jpackage_dmg[@]}"

DMG_PATH="$(find "$DIST_DIR" -maxdepth 1 -name "$APP_NAME*.dmg" -print -quit)"
if [[ -z "$DMG_PATH" ]]; then
  echo "Unable to locate the generated dmg." >&2
  exit 1
fi

if (( can_notarize )); then
  submit_for_notarization "$DMG_PATH"
  xcrun stapler staple "$DMG_PATH"
fi

echo "Packaged artifacts:"
echo "  $APP_IMAGE"
echo "  $DMG_PATH"

if [[ -z "${MOIRA_SIGN_IDENTITY:-}" ]]; then
  echo "Built without signing. Set MOIRA_SIGN_IDENTITY to enable codesigning."
elif (( ! can_notarize )); then
  echo "Built with signing only. Set MOIRA_NOTARY_PROFILE or Apple ID credentials to enable notarization."
fi
