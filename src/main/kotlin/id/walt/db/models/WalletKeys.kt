package id.walt.db.models

import id.walt.core.crypto.keys.Key
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.json.json

object WalletKeys : Table() {
    val account = reference("account", Accounts.id)
    val keyId = varchar("kid", 512)
    val document = text("key")

    override val primaryKey = PrimaryKey(account, keyId)
}
