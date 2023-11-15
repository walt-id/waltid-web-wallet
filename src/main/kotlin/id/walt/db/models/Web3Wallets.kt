package id.walt.db.models

import org.jetbrains.exposed.sql.Table

object Web3Wallets : Table("web3wallets") {
    val account = reference("account", Accounts)
    val id = uuid("id").autoGenerate()

    val address = varchar("address", 256).uniqueIndex()
    val ecosystem = varchar("ecosystem", 128)
    val owner = bool("owner")

    override val primaryKey = PrimaryKey(account, id)
}
