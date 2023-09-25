package id.walt.db.models

import org.jetbrains.exposed.sql.Table

object WalletCredentials : Table() {
    val wallet = reference("wallet", Wallets.id)
    val credentialId = varchar("id", 256)
    val credential = text("credential")

    override val primaryKey = PrimaryKey(wallet, credentialId)
}
