package id.walt.db.models

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp

object WalletOperationHistories : UUIDTable() {
    val account = reference("account", Accounts)
    val timestamp = timestamp("timestamp")
    val operation = varchar("operation", 48)
    val data = text("data")
}