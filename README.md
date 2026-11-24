# SoloType (`v0.0.1-alpha`) — Treinador de Digitação Desktop (Windows, macOS, Linux)

Versão atual: **`v0.0.1-alpha`**

O **SoloType** é um treinador de digitação multiplataforma que detecta automaticamente o teclado instalado, o layout (`ABNT2`, `US QWERTY`, `ES ISO`) e o idioma do sistema, guiando o usuário tecla por tecla com o **dedo exato**, **Shift da mão oposta**, medição de **PPM/WPM**, **CPM**, **taxa de acertos** e desempenho por dedo.

Todos os instaladores já incluem o runtime Java embutido — o usuário final **não precisa instalar Java nem abrir o terminal** no macOS ou Windows.

---

## Instalação para Usuário Final

### macOS (`.dmg` — Arrastar para Aplicativos)
1. Abra o arquivo **`dist/SoloType-1.0.0.dmg`** (ou baixe dos Releases).
2. Arraste o ícone **SoloType** para a pasta **Applications (Aplicativos)**.
3. Abra pelo Launchpad ou Spotlight.
   > *Nota:* caso o app ainda não seja assinado com certificado Apple Developer, na primeira execução clique com o botão direito em `SoloType.app` → **Abrir**.

### Windows (Setup Wizard `.exe` / `.msi`)
1. Execute **`SoloType-1.0.0.exe`** (ou `.msi`).
2. Siga o assistente de instalação (Next → escolher pasta → Install).
3. O instalador cria atalhos na **Área de Trabalho** e no **Menu Iniciar**.

### Linux (Pacote `.deb` ou `./install.sh`)
- **Debian / Ubuntu / Mint (via pacote `.deb`)**:
  - Dê dois cliques no arquivo `solotype_1.0.0_amd64.deb` para instalar pela loja/instalador gráfico, ou pelo terminal:
    ```bash
    sudo apt install ./solotype_1.0.0_amd64.deb
    ```
- **Qualquer distro Linux (via `install.sh`)**:
  ```bash
  chmod +x install.sh
  ./install.sh
  ```
  O script [packaging/linux/install.sh](packaging/linux/install.sh) instala via `.deb` (se presente) ou instala o bundle com JVM embutida em `~/.local/share/solotype` e registra o atalho no menu de aplicativos (`solotype.desktop`).

---

## Como Gerar os Instaladores (Desenvolvedor)

O `jpackage` exige que cada instalador nativo seja compilado no respectivo sistema operacional (ou automaticamente via GitHub Actions em [.github/workflows/release-installers.yml](.github/workflows/release-installers.yml)):

- **macOS (`.dmg` e `.app`)**:
  ```bash
  ./gradlew packageDmg createDistributable
  ```
- **Windows (Setup Wizard `.exe` e `.msi`)** *(requer JDK 17+ e WiX Toolset 3.x)*:
  ```bat
  packaging\windows\build-windows-setup.bat
  ```
- **Linux (`.deb` e bundle para `install.sh`)** *(em um host Debian/Ubuntu)*:
  ```bash
  ./gradlew packageDeb createDistributable
  ```

---

## Licença

Consulte o arquivo [LICENSE](LICENSE) na raiz deste repositório.

