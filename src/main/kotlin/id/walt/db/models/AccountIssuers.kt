package id.walt.db.models

import org.jetbrains.exposed.dao.id.UUIDTable

object AccountIssuers : UUIDTable("account_issuers") {
    val account = reference("account", Accounts)
    val issuer = reference("issuer", Issuers)

    init {
        uniqueIndex(account, issuer)
    }
}