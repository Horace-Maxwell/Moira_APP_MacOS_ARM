#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD_DIR="$ROOT_DIR/build/dev"
CLASS_DIR="$BUILD_DIR/classes"
APP_DIR="$BUILD_DIR/app"
LIB_DIR="$APP_DIR/lib"
MANIFEST_FILE="$BUILD_DIR/MANIFEST.MF"
SOURCE_LIST="$BUILD_DIR/sources.txt"
LAUNCHER="$BUILD_DIR/Moira.sh"

JAVA_BIN="${JAVA_HOME:+$JAVA_HOME/bin/}java"
JAVAC_BIN="${JAVA_HOME:+$JAVA_HOME/bin/}javac"
JAR_BIN="${JAVA_HOME:+$JAVA_HOME/bin/}jar"

if [[ ! -x "$JAVA_BIN" ]]; then
  JAVA_BIN="$(command -v java)"
fi
if [[ ! -x "$JAVAC_BIN" ]]; then
  JAVAC_BIN="$(command -v javac)"
fi
if [[ ! -x "$JAR_BIN" ]]; then
  JAR_BIN="$(command -v jar)"
fi

rm -rf "$BUILD_DIR"
mkdir -p "$CLASS_DIR" "$LIB_DIR"

find "$ROOT_DIR/src" -name '*.java' | sort > "$SOURCE_LIST"
"$JAVAC_BIN" --release 11 -cp "$ROOT_DIR/lib/*" -d "$CLASS_DIR" @"$SOURCE_LIST"

classpath_entries=()
for jar in "$ROOT_DIR"/lib/*.jar; do
  classpath_entries+=("lib/$(basename "$jar")")
done

{
  printf 'Manifest-Version: 1.0\n'
  printf 'Main-Class: org.athomeprojects.moira.Moira\n'
  printf 'Class-Path:'
  for entry in "${classpath_entries[@]}"; do
    printf ' %s' "$entry"
  done
  printf '\n'
} > "$MANIFEST_FILE"

"$JAR_BIN" cfm "$APP_DIR/Moira.jar" "$MANIFEST_FILE" -C "$CLASS_DIR" .
cp "$ROOT_DIR"/lib/*.jar "$LIB_DIR/"
cp -R "$ROOT_DIR/ephe" "$APP_DIR/"
cp -R "$ROOT_DIR/icon" "$APP_DIR/"
cp "$ROOT_DIR"/cities.prop "$APP_DIR/"
cp "$ROOT_DIR"/Moira.ini "$APP_DIR/"
cp "$ROOT_DIR"/moira.ico "$APP_DIR/"
cp "$ROOT_DIR"/moira_s.prop "$APP_DIR/"
cp "$ROOT_DIR"/moira_t.prop "$APP_DIR/"
cp "$ROOT_DIR"/sample_s.prop "$APP_DIR/"
cp "$ROOT_DIR"/sample_t.prop "$APP_DIR/"
if [[ -f "$ROOT_DIR/splash.png" ]]; then
  cp "$ROOT_DIR"/splash.png "$APP_DIR/"
fi
cp "$ROOT_DIR"/WMM*.COF "$APP_DIR/"
cp "$ROOT_DIR"/LICENSE "$APP_DIR/"
cp "$ROOT_DIR"/docs/licenses/LGPL-2.1.txt "$APP_DIR/"
cp "$ROOT_DIR"/docs/licenses/Swiss-Ephemeris-SEPL-0.2.txt "$APP_DIR/"
touch "$APP_DIR/.package"

cat > "$LAUNCHER" <<'EOF'
#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_DIR="$SCRIPT_DIR/app"
JAVA_BIN="${JAVA_HOME:+$JAVA_HOME/bin/}java"
if [[ ! -x "$JAVA_BIN" ]]; then
  JAVA_BIN="$(command -v java)"
fi

exec "$JAVA_BIN" -XstartOnFirstThread -Xdock:name=Moira -jar "$APP_DIR/Moira.jar" "$APP_DIR" "$@"
EOF
chmod +x "$LAUNCHER"

echo "Build complete:"
echo "  App staging dir: $APP_DIR"
echo "  Launcher: $LAUNCHER"

if [[ "${1:-}" == "--run" ]]; then
  shift
  exec "$LAUNCHER" "$@"
fi
