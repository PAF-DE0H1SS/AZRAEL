package xyz.azraellab.shared.core.crypto

import java.util.Base64

actual object Base64Codec {
    actual fun encode(bytes: ByteArray): String = Base64.getEncoder().encodeToString(bytes)
    actual fun decode(str: String): ByteArray = Base64.getDecoder().decode(str)
}