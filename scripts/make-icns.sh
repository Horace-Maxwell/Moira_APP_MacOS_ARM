#!/usr/bin/env bash
set -euo pipefail

SOURCE_ICO="${1:?Usage: scripts/make-icns.sh <source.ico> <dest.icns>}"
DEST_ICNS="${2:?Usage: scripts/make-icns.sh <source.ico> <dest.icns>}"

WORK_DIR="$(mktemp -d)"
ICONSET_DIR="$WORK_DIR/Moira.iconset"
SOURCE_PNG="$WORK_DIR/source.png"

cleanup() {
  rm -rf "$WORK_DIR"
}
trap cleanup EXIT

mkdir -p "$ICONSET_DIR"
sips -s format png "$SOURCE_ICO" --out "$SOURCE_PNG" >/dev/null

for size in 16 32 128 256 512; do
  sips -z "$size" "$size" "$SOURCE_PNG" \
    --out "$ICONSET_DIR/icon_${size}x${size}.png" >/dev/null
  retina_size=$((size * 2))
  sips -z "$retina_size" "$retina_size" "$SOURCE_PNG" \
    --out "$ICONSET_DIR/icon_${size}x${size}@2x.png" >/dev/null
done

mkdir -p "$(dirname "$DEST_ICNS")"
iconutil -c icns "$ICONSET_DIR" -o "$DEST_ICNS"
