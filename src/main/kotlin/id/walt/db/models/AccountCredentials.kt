package id.walt.db.models

import org.jetbrains.exposed.sql.Table

object AccountCredentials : Table() {
    val account = reference("account", Accounts.id)
    val credentialId = varchar("id", 256)
    val credential = text("credential")

    override val primaryKey = PrimaryKey(account, credentialId)
}
