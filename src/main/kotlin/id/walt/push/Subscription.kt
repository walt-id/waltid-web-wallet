package id.walt.push

import kotlinx.serialization.Serializable
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.jce.spec.ECPublicKeySpec
import java.security.KeyFactory
import java.security.PublicKey
import java.util.*

@Serializable
data class Subscription(
    val auth: String,
    val key: String,
    val endpoint: String
) {
    /**
     * Returns the base64 encoded auth string as a byte[]
     */
    fun authAsBytes(): ByteArray = Base64.getDecoder().decode(auth)

    /**
     * Returns the base64 encoded public key string as a byte[]
     */
    fun keyAsBytes(): ByteArray = Base64.getDecoder().decode(key)

    /**
     * Returns the base64 encoded public key as a PublicKey object
     */
    fun userPublicKey(): PublicKey {
        val kf = KeyFactory.getInstance("ECDH", BouncyCastleProvider.PROVIDER_NAME)
        val ecSpec = ECNamedCurveTable.getParameterSpec("secp256r1")
        val point = ecSpec.curve.decodePoint(keyAsBytes())
        val pubSpec = ECPublicKeySpec(point, ecSpec)
        return kf.generatePublic(pubSpec)
    }
}
