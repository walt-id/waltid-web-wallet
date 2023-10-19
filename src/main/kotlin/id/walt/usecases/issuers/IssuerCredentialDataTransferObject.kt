package id.walt.usecases.issuers

import kotlinx.serialization.Serializable

@Serializable
data class IssuerCredentialDataTransferObject(
    val type: String,
    val uiEndpoint: String,
    val callbackUrl: String,
)
