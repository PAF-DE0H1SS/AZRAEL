package xyz.azraellab.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun App(nativeGreeting: () -> String) {
    var counter by remember { mutableStateOf(0) }
    var text by remember { mutableStateOf("") }

    MaterialTheme {
        Scaffold { innerPadding ->
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.padding(innerPadding).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("AZRAEL · Compose Multiplatform", style = MaterialTheme.typography.headlineMedium)
                    Text("Platform: ${platformName()}", style = MaterialTheme.typography.bodyLarge)
                    Text("Native (C++ on Android): ${nativeGreeting()}", style = MaterialTheme.typography.bodyLarge)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(onClick = { counter-- }) { Text("-") }
                        Text("$counter", style = MaterialTheme.typography.titleLarge)
                        Button(onClick = { counter++ }) { Text("+") }
                    }

                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Введите текст") },
                        singleLine = true
                    )
                    Text("Вы ввели: ${text.ifBlank { "(пусто)" }}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}