# AZRAEL

AZRAEL — кроссплатформенное приложение на **Compose Multiplatform** (Kotlin + C++/JNI).

- **Android** (`:app`) — Activity + C++ через CMake/JNI (`nativeString`)
- **Windows 10/11** (`:desktopApp`) — нативный JVM-билд, MSI-инсталлятор
- **Shared UI** (`:composeApp`) — единый Material3-интерфейс (счётчик + текстовое поле + приветствие из нативного кода) для обеих платформ
- **R8** minify + shrink resources в release (debug ~31 MB → release ~15 MB)

## Модули

| Модуль | Тип | Роль |
|--------|-----|------|
| `app` | Android application | C++/CMake/JNI, запускает общий UI |
| `composeApp` | KMP library | общий UI (commonMain): `App.kt`, `expect fun platformName()` |
| `desktopApp` | JVM application | Windows/desktop-обвязка, `packageMsi` |

## Версии

AGP 9.2.1 · Kotlin 2.4.20 · Compose Multiplatform 1.12.1 · compileSdk 37 · minSdk 33 · targetSdk 36 · Gradle 9.4.1 · JDK 17+ (для desktop нужен JDK с `jpackage`).

## Сборка

```bash
# Всё сразу (Android debug+release + desktop)
./gradlew :app:assembleDebug :app:assembleRelease :desktopApp:packageUberJarForCurrentOS :desktopApp:createDistributable

# Windows MSI (только на Windows-хосте)
./gradlew :desktopApp:packageMsi
```

Или скриптами-обёртками:
- Linux/NixOS: `./build-all.sh [dev]` → артефакты в `build/dist/`
- Windows: `build-all.bat` → `build\dist\AZRAEL-win10-11.msi`

CI (GitHub Actions) собирает APK и MSI автоматически на push в `main`/`develop` и на PR.

## Git-flow

| Ветка | Назначение | Сливается в |
|-------|-----------|-------------|
| `main` | стабильный продакшн | — |
| `develop` | интеграция, ветвление фич | `main` (через release/PR) |
| `feature/*` | новые функции | `develop` |
| `release/*` | подготовка релиза | `main` + `develop` |
| `hotfix/*` | срочные фиксы продакшна | `main` + `develop` |

- `main` и `develop` защищены: обязательный PR, 1 approval, обязательные статус-чеки `android` + `windows`, запрещены force-push и delete.
- PR обязателен — прямой push в защищённые ветки запрещён.