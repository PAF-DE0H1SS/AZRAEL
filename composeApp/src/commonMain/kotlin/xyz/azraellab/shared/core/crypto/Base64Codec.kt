package xyz.azraellab.shared.core.crypto

// Ожидаемый контракт Base64-кодирования для платформо-независимого слоя.
expect object Base64Codec {
    fun encode(bytes: ByteArray): String
    fun decode(str: String): ByteArray
}
