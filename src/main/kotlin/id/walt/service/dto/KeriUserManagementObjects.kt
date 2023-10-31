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

@Serializable
data class KeriInceptionRequest (
    val alias: String,
    val passcode: String
)
@Serializable
data class KeriInceptionResponse (
    val aid: String,
    val did: String,
    val publicKeys: List<String>
)

@Serializable
data class KeriRegistryInceptionResponse (
    val scid: String
)

@Serializable
data class KeriOobiResolveRequest (
    val passcode: String,
    val oobiAlias: String,
    val url: String
)
