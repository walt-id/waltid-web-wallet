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