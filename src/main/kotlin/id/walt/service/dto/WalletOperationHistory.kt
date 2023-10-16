package id.walt.service.dto

import id.walt.service.WalletService
import id.walt.utils.JsonUtils.toJsonPrimitives
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class WalletOperationHistory(
    val accountId: String,
    //val walletId: String,
    val timestamp: Instant,
    val operation: String,
    val data: Map<String, JsonElement>
) {
    companion object {
        fun new(wallet: WalletService, operation: String, data: Map<String, Any?>) =
            WalletOperationHistory(wallet.accountId.toString(), Clock.System.now(), operation, data.toJsonPrimitives())
    }
}