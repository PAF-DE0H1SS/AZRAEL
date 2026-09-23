# -----------------------------
# AZRAEL: R8 / ProGuard rules
# -----------------------------

# --- JNI ---
# Нативные методы и их класс (MainActivity) не обфусцировать:
# C++ обращается по имени Java_xyz_azraellab_app_MainActivity_nativeString.
-keepclasseswithmembernames,includedescriptorclasses class xyz.azraellab.app.MainActivity {
    native <methods>;
}
-keep class xyz.azraellab.app.MainActivity { *; }

# Библиотека 'app' (libapp.so) загружается по имени через System.loadLibrary("app").
-keepclasseswithmembernames class * {
    native <methods>;
}

# --- core / общий пакет (Compose Multiplatform shared) ---
-keep class xyz.azraellab.shared.** { *; }

# --- Compose (базовые keep для корректной работы при обфускации) ---
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# --- Kotlin stdlib / metadata ---
-keep class kotlin.Metadata { *; }

# Источник для stack trace (опционально, но полезно при отладке релиза).
-keepattributes SourceFile,LineNumberTable,Signature,InnerClasses,EnclosingMethod