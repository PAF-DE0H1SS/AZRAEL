# AZRAEL-APP

[![Platform: Android 13+](https://img.shields.io/badge/Android-13%2B-3DDC84?style=flat&logo=android&logoColor=white)](https://github.com/PAF-DE0H1SS/AZRAEL-APP)
[![Platform: Windows 10/11](https://img.shields.io/badge/Windows-10%2F11-0078D6?style=flat&logo=windows&logoColor=white)](https://github.com/PAF-DE0H1SS/AZRAEL-APP)
[![Platform: Linux](https://img.shields.io/badge/Linux-deb%20%E2%94%82%20PKGBUILD%20%E2%94%82%20flake-FCC624?style=flat&logo=linux&logoColor=black)](https://github.com/PAF-DE0H1SS/AZRAEL-APP)
[![Language: Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white)](https://github.com/PAF-DE0H1SS/AZRAEL-APP)
[![License: CC BY-NC-SA 4.0](https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0-red?style=flat)](LICENSE)
[![CI: auto-build](https://img.shields.io/github/actions/workflow/status/PAF-DE0H1SS/AZRAEL-APP/build.yml?branch=develop&style=flat&label=CI%20auto-build)](https://github.com/PAF-DE0H1SS/AZRAEL-APP/actions)

> ⚠️ **Status: active development.** The app is in an early stage — it may not work at all, or it may work with bugs and glitches.

**AZRAEL-APP** — a cross-platform app built with Compose Multiplatform that works together with the [azrael-lab.xyz](https://azrael-lab.xyz/) project.

Shared UI lives in `composeApp`, the native layer (C++/JNI) in `app`, and the desktop wrapper in `desktopApp`.

## Language / Язык / 语言

<Tabs>
<Tab label="English">

### ✨ Overview

> ⚠️ **Status: active development.** The app is in an early stage — it may not work at all, or it may work with bugs and glitches.

AZRAEL-APP is a small cross-platform app built with Compose Multiplatform. It connects to and interacts with the azrael-lab.xyz project — the same UI is shared between native Android and desktop builds.

**Supported platforms:**

- 📱 **Android 13+**
- 🖥️ **Windows 10/11** — MSI installer
- 🐧 **Linux** — Ubuntu / Debian (.deb), Arch (PKGBUILD), NixOS (flake)

### 🔨 Build

Android, debug or release:

```
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease
```

Windows MSI (run on a Windows host):

```
./gradlew :desktopApp:packageMsi
```

Linux packages (AppImage + .deb):

```
./gradlew :desktopApp:packageAppImage :desktopApp:packageDeb
```

Or use the single script:

```
./build-all.sh [release|dev|linux]
```

<details>
<summary><b>NixOS</b></summary>

Run directly from the flake:

```
nix run github:PAF-DE0H1SS/AZRAEL-APP
```

Or build locally (network access is required for Gradle):

```
nix build . --option sandbox false
nix run .
```

Development shell with JDK / CMake / Ninja:

```
nix develop
```

</details>

<details>
<summary><b>Arch</b></summary>

```
makepkg -si
```

</details>

### 🌿 Branches

- `main` — stable
- `develop` — integration
- `feature/*`, `release/*`, `hotfix/*` — as needed

### 📄 License

CC BY-NC-SA 4.0 (see [LICENSE](LICENSE)).

</Tab>
<Tab label="Русский">

### ✨ Обзор

> ⚠️ **Статус: активная разработка.** Приложение на ранней стадии — может вообще не работать или работать со сбоями.

AZRAEL-APP — небольшое кроссплатформенное приложение на Compose Multiplatform. Оно взаимодействует с проектом azrael-lab.xyz — один и тот же интерфейс используется и в нативном Android, и в десктопной сборке.

**Поддерживаемые платформы:**

- 📱 **Android 13+**
- 🖥️ **Windows 10/11** — установщик MSI
- 🐧 **Linux** — Ubuntu / Debian (.deb), Arch (PKGBUILD), NixOS (flake)

### 🔨 Сборка

Android, debug или release:

```
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease
```

Windows (MSI собирается на Windows-хосте):

```
./gradlew :desktopApp:packageMsi
```

Linux (AppImage + .deb):

```
./gradlew :desktopApp:packageAppImage :desktopApp:packageDeb
```

Или одним скриптом:

```
./build-all.sh [release|dev|linux]
```

<details>
<summary><b>NixOS</b></summary>

Запуск прямо из флейка:

```
nix run github:PAF-DE0H1SS/AZRAEL-APP
```

Локальная сборка (gradle требует сетевого доступа):

```
nix build . --option sandbox false
nix run .
```

Dev-окружение с JDK / CMake / Ninja:

```
nix develop
```

</details>

<details>
<summary><b>Arch</b></summary>

```
makepkg -si
```

</details>

### 🌿 Ветки

- `main` — стабильная
- `develop` — интеграционная
- `feature/*`, `release/*`, `hotfix/*` — по ситуации

### 📄 Лицензия

CC BY-NC-SA 4.0 (см. [LICENSE](LICENSE)).

</Tab>
<Tab label="中文">

### ✨ 概述

> ⚠️ **状态：积极开发中。** 应用处于早期阶段 — 可能无法运行，也可能存在各种问题。

AZRAEL-APP 是一个基于 Compose Multiplatform 的跨平台应用，与 azrael-lab.xyz 项目协同工作 — Android 原生与桌面版共享同一套界面。

**支持的平台：**

- 📱 **Android 13+**
- 🖥️ **Windows 10/11** — MSI 安装包
- 🐧 **Linux** — Ubuntu / Debian（.deb）、Arch（PKGBUILD）、NixOS（flake）

### 🔨 构建

Android，debug 或 release：

```
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease
```

Windows MSI（请在 Windows 主机上构建）：

```
./gradlew :desktopApp:packageMsi
```

Linux 安装包（AppImage + .deb）：

```
./gradlew :desktopApp:packageAppImage :desktopApp:packageDeb
```

或直接使用统一脚本：

```
./build-all.sh [release|dev|linux]
```

<details>
<summary><b>NixOS</b></summary>

直接从 flake 运行：

```
nix run github:PAF-DE0H1SS/AZRAEL-APP
```

或本地构建（Gradle 需要网络）：

```
nix build . --option sandbox false
nix run .
```

开发环境（JDK / CMake / Ninja）：

```
nix develop
```

</details>

<details>
<summary><b>Arch</b></summary>

```
makepkg -si
```

</details>

### 🌿 分支

- `main` — 稳定版
- `develop` — 集成分支
- `feature/*`、`release/*`、`hotfix/*` — 按需使用

### 📄 许可证

CC BY-NC-SA 4.0（见 [LICENSE](LICENSE)）。

</Tab>
</Tabs>