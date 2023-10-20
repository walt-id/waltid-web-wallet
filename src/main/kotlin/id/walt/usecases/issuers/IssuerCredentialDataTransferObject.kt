package id.walt.usecases.issuers

import kotlinx.serialization.Serializable

@Serializable
data class IssuerCredentialDataTransferObject(
    val id: String,
    val types: List<String>,
)
