#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
POSTGRES_JAR="$HOME/.m2/repository/org/postgresql/postgresql/42.7.11/postgresql-42.7.11.jar"
OUTPUT_DIR="$PROJECT_DIR/target/classes"

mkdir -p "$OUTPUT_DIR"
javac -cp "$POSTGRES_JAR" \
  -d "$OUTPUT_DIR" \
  $(find "$PROJECT_DIR/src/main/java" -name '*.java')

cd "$PROJECT_DIR"
java -cp "$OUTPUT_DIR:$POSTGRES_JAR" Main
