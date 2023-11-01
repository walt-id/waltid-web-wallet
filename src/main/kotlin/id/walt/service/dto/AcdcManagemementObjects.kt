package id.walt.service.dto

import kotlinx.serialization.Serializable

@Serializable
data class AcdcSaidifyRequest (
    val filename: String
)

@Serializable
data class AcdcSaidifyResponse (
    val said: String,
    val file: String
)

@Serializable
data class IpexSaid (
    val said: String
)

@Serializable
data class IpexGrant (
    val passcode: String,
    val credentialSaid: String,
    val recipient: String,
    val message: String? = null
)

@Serializable
data class IpexList (
    val passcode: String,
    val schema: String,
    val type: IPEX_EVENT? = null,
    val poll: Boolean = true,
    val said: Boolean = true,
    val sent: Boolean = false,
    val verbose: Boolean = false
)
enum class IPEX_EVENT(val type: String) {
    APPLY("apply"),
    OFFER("offer"),
    AGREE("agree"),
    GRANT("grant"),
    SUBMIT("submit")
}