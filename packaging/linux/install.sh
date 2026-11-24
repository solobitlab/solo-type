#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_NAME="SoloType"
BIN_NAME="solotype"

echo "=========================================="
echo "   Instalador SoloType para Linux"
echo "=========================================="

# 1. Se houver um pacote .deb junto ao script e o sistema suportar apt/dpkg, instala via pacote Debian
DEB_FILE="$(find "$SCRIPT_DIR" -maxdepth 2 -name "solotype_*.deb" 2>/dev/null | head -n 1 || true)"
if [[ -n "$DEB_FILE" ]] && command -v dpkg >/dev/null 2>&1; then
    echo "Pacote Debian encontrado: $DEB_FILE"
    if command -v apt-get >/dev/null 2>&1; then
        echo "Instalando via apt (pode solicitar senha sudo)..."
        sudo apt-get install -y "$DEB_FILE"
    else
        echo "Instalando via dpkg (pode solicitar senha sudo)..."
        sudo dpkg -i "$DEB_FILE" || sudo apt-get install -f -y
    fi
    echo "SoloType instalado com sucesso! Procure por 'SoloType' no menu de aplicativos."
    exit 0
fi

# 2. Caso contrario, instala o pacote portatil (com JVM embutida) sem precisar de Java no sistema
BUNDLE_DIR=""
if [[ -d "$SCRIPT_DIR/SoloType" ]]; then
    BUNDLE_DIR="$SCRIPT_DIR/SoloType"
elif [[ -d "$SCRIPT_DIR/app/SoloType" ]]; then
    BUNDLE_DIR="$SCRIPT_DIR/app/SoloType"
elif [[ -x "$SCRIPT_DIR/bin/SoloType" ]]; then
    BUNDLE_DIR="$SCRIPT_DIR"
fi

if [[ -z "$BUNDLE_DIR" ]]; then
    echo "Erro: nenhum pacote .deb ou diretório SoloType encontrado junto a este install.sh."
    echo "Execute './gradlew packageDeb createDistributable' em um Linux antes de distribuir."
    exit 1
fi

if [[ "${EUID:-$(id -u)}" -eq 0 ]]; then
    INSTALL_DIR="/opt/solotype"
    BIN_DIR="/usr/local/bin"
    DESKTOP_DIR="/usr/share/applications"
    ICON_DIR="/usr/share/icons/hicolor/512x512/apps"
else
    INSTALL_DIR="$HOME/.local/share/solotype"
    BIN_DIR="$HOME/.local/bin"
    DESKTOP_DIR="$HOME/.local/share/applications"
    ICON_DIR="$HOME/.local/share/icons/hicolor/512x512/apps"
fi

echo "Instalando arquivos em: $INSTALL_DIR"
rm -rf "$INSTALL_DIR"
mkdir -p "$INSTALL_DIR" "$BIN_DIR" "$DESKTOP_DIR" "$ICON_DIR"
cp -a "$BUNDLE_DIR"/. "$INSTALL_DIR"/

EXEC_PATH="$INSTALL_DIR/bin/SoloType"
if [[ ! -x "$EXEC_PATH" ]]; then
    EXEC_PATH="$(find "$INSTALL_DIR/bin" -maxdepth 1 -type f -executable | head -n 1)"
fi
chmod +x "$EXEC_PATH"
ln -sf "$EXEC_PATH" "$BIN_DIR/$BIN_NAME"

ICON_SRC="$(find "$INSTALL_DIR" -name "*.png" | head -n 1 || true)"
ICON_TARGET="solotype"
if [[ -n "$ICON_SRC" ]]; then
    cp "$ICON_SRC" "$ICON_DIR/solotype.png"
    ICON_TARGET="$ICON_DIR/solotype.png"
fi

cat > "$DESKTOP_DIR/solotype.desktop" <<EOF
[Desktop Entry]
Type=Application
Name=$APP_NAME
Comment=Treinador de Digitação com os Dedos Corretos
Exec=$EXEC_PATH
Icon=$ICON_TARGET
Terminal=false
Categories=Education;Utility;
StartupWMClass=com-solotype-MainKt
EOF

chmod +x "$DESKTOP_DIR/solotype.desktop"
command -v update-desktop-database >/dev/null 2>&1 && update-desktop-database "$DESKTOP_DIR" >/dev/null 2>&1 || true

echo ""
echo "SoloType instalado com sucesso!"
echo "- Abra pelo menu de aplicativos ('SoloType')"
echo "- Ou execute no terminal: $BIN_NAME"
