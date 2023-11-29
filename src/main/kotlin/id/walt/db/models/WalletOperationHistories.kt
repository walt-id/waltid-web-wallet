package id.walt.db.models

import id.walt.service.WalletService
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDTable
import kotlinx.uuid.generateUUID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.timestamp

object WalletOperationHistories : KotlinxUUIDTable("wallet_operation_histories") {
    val account = reference("account", Accounts)
    val wallet = reference("wallet", Wallets)
    val timestamp = timestamp("timestamp")
    val operation = varchar("operation", 48)
    val data = text("data")
}

@Serializable
data class WalletOperationHistory(
    val id: UUID? = UUID.generateUUID(),
    val account: UUID,
    val wallet: UUID,
    val timestamp: Instant,
    val operation: String,
    val data: String,
) {
    constructor(result: ResultRow) : this(
        id = result[WalletOperationHistories.id].value,
        account = result[WalletOperationHistories.account].value,
        wallet = result[WalletOperationHistories.wallet].value,
        timestamp = result[WalletOperationHistories.timestamp].toKotlinInstant(),
        operation = result[WalletOperationHistories.operation],
        data = result[WalletOperationHistories.data],
    )

    companion object {
        fun new(wallet: WalletService, operation: String, data: Map<String, String?>) = WalletOperationHistory(
            account = wallet.accountId,
            wallet = wallet.walletId,
            timestamp = Clock.System.now(),
            operation = operation,
            data = Json.encodeToString(data)
        )
    }
}
