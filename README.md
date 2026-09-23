# AZRAEL-APP

Cross-platform application that works together with the [azrael-lab.xyz](https://azrael-lab.xyz/) project.

Shared UI lives in `composeApp`, the native layer (C++/JNI) in `app`, and the desktop wrapper in `desktopApp`.

<Tabs>
<Tab label="English">

## Overview

AZRAEL-APP is a small cross-platform app built with Compose Multiplatform. It connects to and interacts with the azrael-lab.xyz project — the same UI is shared between native Android and desktop builds.

Platforms:

- Android 13+
- Windows 10/11 (MSI installer)
- Linux: Ubuntu / Debian (.deb), Arch (PKGBUILD), NixOS (flake)

## Build

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

Or use the single script: `./build-all.sh [release|dev|linux]`.

### NixOS

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

### Arch

```
makepkg -si
```

## Branches

- `main` — stable
- `develop` — integration
- `feature/*`, `release/*`, `hotfix/*` — as needed

License: CC BY-NC-SA 4.0 (see `LICENSE`).

</Tab>
<Tab label="Русский">

## Обзор

AZRAEL-APP — небольшое кроссплатформенное приложение на Compose Multiplatform. Оно взаимодействует с проектом azrael-lab.xyz — один и тот же интерфейс используется и в нативном Android, и в десктопной сборке.

Платформы:

- Android 13+
- Windows 10/11 (установщик MSI)
- Linux: Ubuntu / Debian (.deb), Arch (PKGBUILD), NixOS (flake)

## Сборка

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

Или одним скриптом: `./build-all.sh [release|dev|linux]`.

### NixOS

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

### Arch

```
makepkg -si
```

## Ветки

- `main` — стабильная
- `develop` — интеграционная
- `feature/*`, `release/*`, `hotfix/*` — по ситуации

Лицензия: CC BY-NC-SA 4.0 (см. `LICENSE`).

</Tab>
<Tab label="中文">

## 概述

AZRAEL-APP 是一个基于 Compose Multiplatform 的跨平台应用，与 azrael-lab.xyz 项目协同工作 — Android 原生与桌面版共享同一套界面。

支持的平台：

- Android 13+
- Windows 10/11（MSI 安装包）
- Linux：Ubuntu / Debian（.deb）、Arch（PKGBUILD）、NixOS（flake）

## 构建

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

或直接使用统一脚本：`./build-all.sh [release|dev|linux]`。

### NixOS

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

### Arch

```
makepkg -si
```

## 分支

- `main` — 稳定版
- `develop` — 集成分支
- `feature/*`、`release/*`、`hotfix/*` — 按需使用

许可证：CC BY-NC-SA 4.0（见 `LICENSE`）。

</Tab>
</Tabs>