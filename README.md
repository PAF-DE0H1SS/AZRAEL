# AZRAEL-APP

Кроссплатформенный клиент для управления Proxmox на Compose Multiplatform.

Платформы:

- Android 13+
- Windows 10/11 (MSI)
- Linux: Ubuntu / Debian (.deb), Arch (PKGBUILD), NixOS (flake)

Общая часть — в `composeApp`, нативный слой (C++/JNI) — в `app`,
десктопная обвязка — в `desktopApp`.

## Сборка

Android (debug / release):

```
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease
```

Windows (MSI, собирается на Windows-хосте):

```
./gradlew :desktopApp:packageMsi
```

Linux (AppImage + .deb):

```
./gradlew :desktopApp:packageAppImage :desktopApp:packageDeb
```

Единый скрипт: `./build-all.sh [release|dev|linux]`.

### NixOS

Внешний флейк (github:NixOS/nixpkgs):

```
nix run github:PAF-DE0H1SS/AZRAEL-APP
```

Или локально (нужен сетевой доступ для gradle):

```
nix build . --option sandbox false
nix run .
```

Dev-окружение с JDK/cmake/ninja:

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