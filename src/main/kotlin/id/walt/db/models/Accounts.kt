package id.walt.db.models

import org.jetbrains.exposed.dao.id.UUIDTable

object Accounts : UUIDTable("accounts") {
    val email = reference("email", Emails).nullable()
    val wallet = reference("wallet", Wallets).nullable()

    init {
        //TODO: constraint to single either email or wallet
        uniqueIndex(email, wallet)
    }
}
