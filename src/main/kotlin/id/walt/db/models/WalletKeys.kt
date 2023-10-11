package id.walt.db.models

import org.jetbrains.exposed.sql.Table

object WalletKeys : Table() {
    val account = reference("account", Accounts.id)
    val keyId = varchar("kid", 512)
    val document = text("key")

    override val primaryKey = PrimaryKey(account, keyId)
}
