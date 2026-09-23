// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
}

// Одна команда для сборки всех платформ с одинаковым кодом/дизайном:
//   ./gradlew autoBuild
// Android: :app (APK debug+release, C++/JNI, R8) + :composeApp android-таргет.
// Desktop : :desktopApp собирает дистрибутив ТЕКУЩЕЙ ОС + перекроссплатформенный
// uber-JAR (запускается на Windows 10/11, macOS и Linux).
tasks.register("autoBuild") {
    group = "build"
    description = "Собирает Android (debug+release) и Desktop (дистрибутив текущей ОС + uber-JAR)"
    dependsOn(
        ":app:assembleDebug",
        ":app:assembleRelease",
        ":desktopApp:createDistributable",
        ":desktopApp:packageUberJarForCurrentOS"
    )
}

// Только установочные релизные артефакты (для CI).
tasks.register("autoBuildRelease") {
    group = "build"
    description = "Собирает только релизные артефакты: Android release + desktop uber-JAR"
    dependsOn(
        ":app:assembleRelease",
        ":desktopApp:packageReleaseUberJarForCurrentOS"
    )
}