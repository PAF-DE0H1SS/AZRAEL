package xyz.azraellab.shared.core.protocol

import java.net.HttpURLConnection
import java.net.URL

// HTTP POST на Android: HttpURLConnection с таймаутами; при не-2xx возвращается null.
actual fun httpPostJson(url: String, body: String, timeoutMs: Int): String? = try {
    val conn = URL(url).openConnection() as HttpURLConnection
    conn.requestMethod = "POST"
    conn.connectTimeout = timeoutMs
    conn.readTimeout = timeoutMs
    conn.setRequestProperty("Content-Type", "application/json")
    conn.doOutput = true
    conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
    val code = conn.responseCode
    val stream = if (code in 200..299) conn.inputStream else conn.errorStream
    val text = stream?.use { String(it.readBytes(), Charsets.UTF_8) }.orEmpty()
    conn.disconnect()
    if (code in 200..299) text else null
} catch (e: Exception) {
    null
}

// На Android URL шлюза задаётся на странице настроек (не хардкодится).
actual fun defaultGatewayUrl(): String? = null