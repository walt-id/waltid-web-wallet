package id.walt.db.models

import org.jetbrains.exposed.sql.Table

object WalletDids: Table() {
    val wallet = reference("wallet", Wallets.id)
    val did = varchar("did", 1024)
    val document = text("document")

    override val primaryKey = PrimaryKey(wallet, did)
}
