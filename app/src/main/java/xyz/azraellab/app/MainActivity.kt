package xyz.azraellab.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import xyz.azraellab.shared.App

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App(nativeGreeting = { nativeString() })
        }
    }

    private external fun nativeString(): String

    companion object {
        init {
            System.loadLibrary("app")
        }
    }
}