package xyz.azraellab.shared.core.protocol

import xyz.azraellab.shared.core.crypto.Base64Codec
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

// Сырой ответ handshake от шлюза: srv_pub + токен сессии (payload — base64(JSON), не зашифрован).
data class HandshakeReply(val serverPubB64: String, val sessionToken: String)

class GatewayClient(private val baseUrl: String, private val box: SessionBox = SessionBox()) {

    fun connect(): HandshakeReply? =
        runCatching {
            val reply = httpPostJson(baseUrl, json.encodeToString(Envelope.serializer(), box.startHandshake()))
                ?: return null
            val env = json.decodeFromString(Envelope.serializer(), reply)
            if (env.err != Protocol.ERR_OK) return null
            val payloadJson = String(Base64Codec.decode(env.payload))
            val obj = json.parseToJsonElement(payloadJson).jsonObject
            val pub = obj["srv_pub"]?.jsonPrimitive?.content ?: return null
            val token = obj["session"]?.jsonPrimitive?.content ?: return null
            HandshakeReply(pub, token)
        }.getOrNull()

    fun sayStatus(): String? {
        val reply = box.seal(Protocol.OP_STATUS, """{"want":"status"}""".toByteArray()) ?: return null
        return openBytes(reply)
    }

    fun sayHello(): String? {
        val reply = box.seal(Protocol.OP_HELLO, """{"want":"echo"}""".toByteArray()) ?: return null
        return openBytes(reply)
    }

    fun disconnect(): Boolean {
        val reply = box.seal(Protocol.OP_SESSION_BYE, "{}".toByteArray()) ?: return false
        return openBytes(reply) != null
    }

    private fun openBytes(request: Envelope): String? {
        val respRaw = httpPostJson(baseUrl, json.encodeToString(Envelope.serializer(), request)) ?: return null
        val resp = json.decodeFromString(Envelope.serializer(), respRaw)
        if (resp.err != Protocol.ERR_OK) return null
        val plain = box.open(resp) ?: return null
        return String(plain)
    }

    private val json = Json { ignoreUnknownKeys = true }
}