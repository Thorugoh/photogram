import java.security.MessageDigest

fun MessageDigest.digest(data: ByteArray): ByteArray {
    update(data)
    return digest()
}