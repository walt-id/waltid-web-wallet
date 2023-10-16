package id.walt.db.models

import org.jetbrains.exposed.dao.id.UUIDTable

object AccountKeys : UUIDTable() {
    val account = reference("account", Accounts)
    val key = reference("key", Keys)
}
