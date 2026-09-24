package xyz.azraellab.shared.core.protocol

import xyz.azraellab.shared.core.crypto.Base64Codec
import xyz.azraellab.shared.core.crypto.Crypto
import xyz.azraellab.shared.core.crypto.KeyPairData
import xyz.azraellab.shared.core.crypto.randomBytes

/**
 * Локальная сессия защищённого канала. Не содержит никаких секретов/адресов —
 * только криптографическое состояние. Транспорт (URL/HTTP) инжектится снаружи.
 */
class SessionBox {
    val keyPair: KeyPairData = Crypto.keyPair()
    private var serverPublicKey: ByteArray? = null
    private var sessionToken: String = ""
    private var envelopeCounter: Long = 0

    val hasSession: Boolean get() = sessionToken.isNotEmpty() && serverPublicKey != null

    fun startHandshake(): Envelope {
        val payloadJson = """{"eph_pub":"${b64(keyPair.publicKey)}"}"""
        return Envelope(
            id = newId(),
            ts = nowSec(),
            op = Protocol.OP_SESSION_INIT,
            session = "",
            payload = b64(payloadJson.toByteArray())
        )
    }

    fun acceptServer(serverPubB64: String, token: String): Boolean {
        if (serverPubB64.isEmpty() || token.isEmpty()) return false
        serverPublicKey = try {
            Base64Codec.decode(serverPubB64)
        } catch (e: Exception) { return false }
        sessionToken = token
        return true
    }

    fun seal(op: String, plainBody: ByteArray): Envelope? {
        val serverPub = serverPublicKey ?: return null
        if (!hasSession) return null
        val shared = Crypto.sharedSecret(keyPair.privateKey, serverPub)
        val nonce = randomBytes(12)
        val aad = buildAAD(op, envelopeCounter)
        val tag = Crypto.encrypt(shared, aad, plainBody, nonce) ?: return null
        envelopeCounter++
        return Envelope(
            id = newId(),
            ts = nowSec(),
            op = op,
            session = sessionToken,
            payload = b64(tag),
            nonce = b64(nonce)
        )
    }

    fun open(env: Envelope): ByteArray? {
        val serverPub = serverPublicKey ?: return null
        val shared = Crypto.sharedSecret(keyPair.privateKey, serverPub)
        val cipher = try { Base64Codec.decode(env.payload) } catch (e: Exception) { return null }
        val nonce = try { Base64Codec.decode(env.nonce) } catch (e: Exception) { return null }
        val aad = serverAAD(env)
        return Crypto.decrypt(shared, aad, cipher, nonce)
    }

    private fun buildAAD(op: String, counter: Long): ByteArray =
        listOf(op, counter.toString(), Protocol.VERSION.toString()).joinToString("|").toByteArray()

    private fun serverAAD(env: Envelope): ByteArray =
        listOf(env.op, env.id, env.ts.toString(), Protocol.VERSION.toString()).joinToString("|").toByteArray()

    private fun newId(): String {
        val r = randomBytes(16)
        return b64(r).replace("+", "-").replace("/", "_").substring(0, 22)
    }

    private fun nowSec(): Long = (System.currentTimeMillis() / 1000)
}

private fun b64(bytes: ByteArray): String = Base64Codec.encode(bytes)