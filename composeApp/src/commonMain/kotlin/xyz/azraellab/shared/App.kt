package xyz.azraellab.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.azraellab.shared.core.protocol.defaultGatewayUrl
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Cottage
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Settings
import xyz.azraellab.shared.ui.AzraelCyan
import xyz.azraellab.shared.ui.AzraelRose
import xyz.azraellab.shared.ui.AzraelTheme
import xyz.azraellab.shared.ui.AzraelViolet
import xyz.azraellab.shared.ui.GlassBackground
import xyz.azraellab.shared.ui.glass

// Пункты навигации: одинаковый набор для бокового rail (ПК) и нижнего бара (телефон).
private data class RailItem(val label: String, val icon: ImageVector)

private val mainRails = listOf(
    RailItem("Главная", Icons.Filled.Cottage),
    RailItem("Профиль", Icons.AutoMirrored.Filled.Chat),
    RailItem("Настройки", Icons.Filled.Settings)
)

@Composable
fun App(nativeGreeting: () -> String) {
    var selected by remember { mutableStateOf(0) }

    AzraelTheme {
        GlassBackground {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                // Ширина >= 700dp считаем "широким" экраном (планшет/ПК): боковой rail,
                // иначе — телефон: нижняя навигация. Высота влияет только на размер заголовка.
                val wide = maxWidth >= 700.dp
                val tall = maxHeight >= 560.dp
                var counter by remember { mutableStateOf(0) }
                var text by remember { mutableStateOf("") }

                if (wide) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        SideRail(
                            selected = selected,
                            onSelect = { selected = it },
                            modifier = Modifier.fillMaxHeight()
                        )
                        MainPane(
                            index = selected,
                            counter = counter,
                            onCounter = { counter = it },
                            text = text,
                            onText = { text = it },
                            nativeGreeting = nativeGreeting,
                            wide = wide,
                            tall = tall
                        )
                    }
                } else {
                    Scaffold(
                        containerColor = Color.Transparent,
                        bottomBar = {
                            BottomBar(selected = selected, onSelect = { selected = it })
                        }
                    ) { padding ->
                        MainPane(
                            index = selected,
                            counter = counter,
                            onCounter = { counter = it },
                            text = text,
                            onText = { text = it },
                            nativeGreeting = nativeGreeting,
                            wide = wide,
                            tall = tall,
                            modifier = Modifier.padding(padding)
                        )
                    }
                }
            }
        }
    }
}

// Нижняя навигация для телефонов: три пункта поверх стеклянной подложки.
@Composable
private fun BottomBar(selected: Int, onSelect: (Int) -> Unit) {
    SurfaceGlass(shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)) {
        NavigationBar(containerColor = Color.Transparent) {
            mainRails.forEachIndexed { idx, item ->
                NavigationBarItem(
                    selected = selected == idx,
                    onClick = { onSelect(idx) },
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }
    }
}

// Боковой NavigationRail для широких экранов: логотип, пункты, индикатор статуса.
@Composable
private fun RowScope.SideRail(selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    SurfaceGlass(shape = RoundedCornerShape(24.dp), modifier = modifier.padding(12.dp)) {
        NavigationRail(containerColor = Color.Transparent) {
            Spacer(Modifier.height(10.dp))
            Text(
                "AZRAEL-APP",
                modifier = Modifier.padding(horizontal = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AzraelViolet
            )
            Spacer(Modifier.height(14.dp))
            mainRails.forEachIndexed { idx, item ->
                NavigationRailItem(
                    selected = selected == idx,
                    onClick = { onSelect(idx) },
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label, style = MaterialTheme.typography.labelSmall) }
                )
            }
            Spacer(Modifier.weight(1f))
            StatusDot()
        }
    }
}

// Основная рабочая область: переключение между разделами по индексу навигации.
@Composable
private fun MainPane(
    index: Int,
    counter: Int,
    onCounter: (Int) -> Unit,
    text: String,
    onText: (String) -> Unit,
    nativeGreeting: () -> String,
    wide: Boolean,
    tall: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().padding(16.dp)) {
        when (index) {
            0 -> HomeScreen(counter, onCounter, text, onText, nativeGreeting, wide, tall)
            1 -> PlaceholderScreen("Профиль", "Здесь будет профиль и синхронизация через защищённый канал.")
            else -> SettingsScreen()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HomeScreen(
    counter: Int,
    onCounter: (Int) -> Unit,
    text: String,
    onText: (String) -> Unit,
    nativeGreeting: () -> String,
    wide: Boolean,
    tall: Boolean
) {
    val headingStyle = if (tall) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleLarge
    val fieldWidth = if (wide) Modifier.width(360.dp) else Modifier.fillMaxWidth()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "AZRAEL-APP",
            style = headingStyle,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Compose Multiplatform · ${platformName()}",
            style = MaterialTheme.typography.bodyMedium,
            color = AzraelCyan
        )

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Label("Платформа")
                Text(nativeGreeting(), style = MaterialTheme.typography.titleMedium)
            }
        }

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text("Счётчик", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AccentButton("−", onClick = { onCounter(counter - 1) })
                CounterPill("$counter")
                AccentButton("+", onClick = { onCounter(counter + 1) })
            }
        }

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text("Ввод", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = text,
                onValueChange = onText,
                label = { Text("Введите текст") },
                singleLine = true,
                modifier = fieldWidth,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AzraelViolet,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.25f)
                )
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Вы ввели: ${text.ifBlank { "(пусто)" }}",
                style = MaterialTheme.typography.bodyMedium,
                color = AzraelRose
            )
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f))
    }
}

@Composable
private fun SettingsScreen() {
    // URL шлюза не хардкодится: подставляется из runtime-конфига (env/настройки) или вручную.
    var gateway by remember { mutableStateOf(defaultGatewayUrl() ?: "") }
    var statusLine by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var channel by remember { mutableStateOf<xyz.azraellab.shared.core.protocol.GatewayClient?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Настройки", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Защищённый канал", style = MaterialTheme.typography.titleMedium)

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Label("Шлюз (runtime-конфиг)")
                OutlinedTextField(
                    value = gateway,
                    onValueChange = { gateway = it },
                    label = { Text("https://…/gateway/v1") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AzraelViolet,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.25f)
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AccentButton("Подключиться") {
                        statusLine = "…"
                        scope.launch {
                            channel = null
                            statusLine = "Проверяю…"
                            val client = xyz.azraellab.shared.core.protocol.GatewayClient(gateway)
                            val hs = withContext(Dispatchers.IO) { client.connect() }
                            channel = if (hs != null) client else null
                            statusLine = if (hs != null) "Канал установлен (ключ X25519 обменян)"
                            else "Сбой рукопожатия: шлюз недоступен или отклонён"
                        }
                    }
                    AccentButton("Статус") {
                        val c = channel
                        if (c == null) statusLine = "Нет активного канала"
                        else scope.launch {
                            statusLine = "…"
                            val reply = withContext(Dispatchers.IO) { c.sayStatus() }
                            statusLine = reply?.let { "Шлюз: $it" } ?: "Ошибка status"
                        }
                    }
                }
                Text(statusLine, style = MaterialTheme.typography.bodyMedium, color = AzraelCyan)
            }
        }

        Text(
            "Защита: X25519 (ECDH) → AES-256-GCM, Envelope с подпись AAD (op|id|ts|v), " +
                "анти-реплей, skew ±120 c. Ключ клиента генерируется при запуске и никуда не уходит.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun SurfaceGlass(
    shape: RoundedCornerShape,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.glass()) {
        content()
    }
}

@Composable
private fun GlassCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier.glass().padding(18.dp)) {
        content()
    }
}

@Composable
private fun Label(text: String) {
    Text(text, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.6f))
}

@Composable
private fun CounterPill(value: String) {
    Box(
        modifier = Modifier
            .height(46.dp)
            .width(84.dp)
            .background(AzraelViolet.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
            .border(1.dp, AzraelCyan.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AccentButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AzraelViolet.copy(alpha = 0.3f),
            contentColor = Color.White
        )
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun StatusDot() {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .width(10.dp)
                .height(10.dp)
                .background(AzraelCyan, CircleShape)
        )
        Text("онлайн", style = MaterialTheme.typography.labelSmall, color = AzraelCyan)
    }
}