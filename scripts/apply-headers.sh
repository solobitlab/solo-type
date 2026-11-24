#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
HEADER_FILE="$ROOT_DIR/scripts/license-header.txt"

if [[ ! -f "$HEADER_FILE" ]]; then
    echo "Erro: arquivo de cabeçalho não encontrado em $HEADER_FILE" >&2
    exit 1
fi

apply_header() {
    local file="$1"
    local first_line
    first_line="$(head -n 1 "$file" || true)"
    if [[ "$first_line" == "/*"* ]]; then
        return 0
    fi

    local tmp_file
    tmp_file="$(mktemp)"
    cat "$HEADER_FILE" > "$tmp_file"
    printf '\n' >> "$tmp_file"
    cat "$file" >> "$tmp_file"
    mv "$tmp_file" "$file"
    echo "Cabeçalho aplicado: ${file#$ROOT_DIR/}"
}

for kts in "$ROOT_DIR/build.gradle.kts" "$ROOT_DIR/settings.gradle.kts"; do
    if [[ -f "$kts" ]]; then
        apply_header "$kts"
    fi
done

while IFS= read -r -d '' kt_file; do
    apply_header "$kt_file"
done < <(find "$ROOT_DIR/src" -type f -name "*.kt" -print0 | sort -z)

echo "Concluído."
