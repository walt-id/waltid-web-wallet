package id.walt.service.dto

import kotlinx.serialization.Serializable

@Serializable
data class WatchedWalletDataTransferObject(
    val id: String,
    val address: String,
    val ecosystem: String,
    val owner: Boolean,
)
