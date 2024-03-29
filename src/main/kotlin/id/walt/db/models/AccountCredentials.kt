package id.walt.db.models

import org.jetbrains.exposed.dao.id.UUIDTable

object AccountCredentials : UUIDTable("account_credentials") {
    val account = reference("account", Accounts)
    val credential = reference("credential", Credentials)
}
