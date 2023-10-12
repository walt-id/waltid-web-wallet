package id.walt.db.models

import org.jetbrains.exposed.sql.Table

object AccountDids: Table() {
    val account = reference("account", Accounts.id)
    val did = varchar("did", 1024)
    val keyId = reference("keyId", AccountKeys.keyId)
    val document = text("document")
    val alias = varchar("alias", 1024)
    val default = bool("default").default(false)

    override val primaryKey = PrimaryKey(account, did)
}
