package id.walt.service.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConnectedWalletDataTransferObject(
    val id: String,
    val address: String,
    val ecosystem: String,
)
