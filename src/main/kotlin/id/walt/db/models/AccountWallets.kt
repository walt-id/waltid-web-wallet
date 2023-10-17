package id.walt.db.models

import org.jetbrains.exposed.dao.id.UUIDTable

object AccountWallets : UUIDTable("account_wallets") {
    val account = reference("account", Accounts)
    val wallet = reference("wallet", Wallets)
    val owner = bool("owner")

    init {
        uniqueIndex(account, wallet)
    }
}
