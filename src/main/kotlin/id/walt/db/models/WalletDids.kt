package id.walt.db.models

import org.jetbrains.exposed.sql.Table

object WalletDids: Table() {
    val account = reference("account", Accounts.id)
    val did = varchar("did", 1024)
    val keyId = reference("keyId", WalletKeys.keyId)
    val document = text("document")

    override val primaryKey = PrimaryKey(account, did)
}