package xyz.azraellab.shared.core.protocol

// Платформенный HTTP POST (JSON). Возвращает строку ответа или null при сетевой/HTTP ошибке.
expect fun httpPostJson(url: String, body: String, timeoutMs: Int = 10_000): String?

// Базовый URL шлюза из runtime-конфига (не хардкодится в коде).
expect fun defaultGatewayUrl(): String?