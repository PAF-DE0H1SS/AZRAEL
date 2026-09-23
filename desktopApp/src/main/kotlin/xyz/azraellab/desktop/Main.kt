package xyz.azraellab.desktop

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import xyz.azraellab.shared.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "AZRAEL",
        state = rememberWindowState(width = 720.dp, height = 480.dp)
    ) {
        App(nativeGreeting = { "not available on desktop (JNI is Android-only)" })
    }
}