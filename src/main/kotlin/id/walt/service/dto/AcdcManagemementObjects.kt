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

enum class IPEX_EVENT(val type: String) {
    APPLY("apply"),
    OFFER("offer"),
    AGREE("agree"),
    GRANT("grant"),
    SUBMIT("submit")
}