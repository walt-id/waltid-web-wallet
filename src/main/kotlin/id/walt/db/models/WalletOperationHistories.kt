package id.walt.db.models

import id.walt.service.WalletService
import id.walt.utils.JsonUtils.toJsonPrimitives
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp

object WalletOperationHistories : UUIDTable("\"wallet_operation_histories\"") {
    val account = reference("account", Accounts.id)
    val timestamp = timestamp("timestamp")
    val operation = varchar("operation", 48)
    val data = text("data")
}

@Serializable
data class WalletOperationHistory(
    val accountId: String,
    val timestamp: Instant,
    val operation: String,
    val data: Map<String, JsonElement>
) {
    companion object {
        fun new(wallet: WalletService, operation: String, data: Map<String, Any?>) =
            WalletOperationHistory(wallet.accountId.toString(), Clock.System.now(), operation, data.toJsonPrimitives())
    }
}
