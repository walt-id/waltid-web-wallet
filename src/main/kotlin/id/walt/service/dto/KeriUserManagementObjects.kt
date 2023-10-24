package id.walt.service.dto

import kotlinx.serialization.Serializable

@Serializable
data class KeriCreateDbRequest (
    val passcode: String
)

@Serializable
data class KeriCreateDbResponse (
    val salt: String,
    val witnesses: List<String>
)
