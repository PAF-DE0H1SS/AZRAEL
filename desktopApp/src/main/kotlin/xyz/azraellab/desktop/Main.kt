package xyz.azraellab.desktop

import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import xyz.azraellab.shared.App

// Точка входа десктопного клиента AZRAEL-APP.
// Общий UI (App) берётся из composeApp; JNI (C++) в desktop недоступен,
// поэтому вместо нативного приветствия передаётся fallback-строка.
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "AZRAEL-APP",
        state = rememberWindowState(width = 720.dp, height = 480.dp),
        icon = desktopAppIcon()
    ) {
        App(nativeGreeting = { "JNI is Android-only" })
    }
}

// Иконка окна читается из ресурсов модуля desktopApp (icon.png, фавикон сайта).
// При отсутствии файла используется системная иконка окна.
private fun desktopAppIcon(): Painter? {
    val bytes = Thread.currentThread().contextClassLoader
        ?.getResourceAsStream("icon.png")?.use { it.readBytes() } ?: return null
    return try {
        org.jetbrains.skia.Image.makeFromEncoded(bytes).use {
            BitmapPainter(it.toComposeImageBitmap())
        }
    } catch (e: Exception) {
        null
    }
}