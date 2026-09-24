package xyz.azraellab.shared.core.crypto

expect object Base64Codec {
    fun encode(bytes: ByteArray): String
    fun decode(str: String): ByteArray
}