package id.walt.db.models

import org.jetbrains.exposed.dao.id.UUIDTable

object AccountDids: UUIDTable() {
    val account = reference("account", Accounts)
    val did = reference("did", Dids)
    val alias = varchar("alias", 1024)
    val default = bool("default").default(false)
}
