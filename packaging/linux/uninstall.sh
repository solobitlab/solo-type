#!/usr/bin/env bash
set -euo pipefail

echo "Removendo SoloType..."

if command -v dpkg >/dev/null 2>&1 && dpkg -s solotype >/dev/null 2>&1; then
    sudo apt-get remove -y solotype || sudo dpkg -r solotype
    echo "Pacote Debian solotype removido."
    exit 0
fi

rm -rf "$HOME/.local/share/solotype"
rm -f "$HOME/.local/bin/solotype"
rm -f "$HOME/.local/share/applications/solotype.desktop"
rm -f "$HOME/.local/share/icons/hicolor/512x512/apps/solotype.png"

if [[ "${EUID:-$(id -u)}" -eq 0 ]]; then
    rm -rf /opt/solotype
    rm -f /usr/local/bin/solotype
    rm -f /usr/share/applications/solotype.desktop
    rm -f /usr/share/icons/hicolor/512x512/apps/solotype.png
fi

echo "SoloType removido com sucesso."
