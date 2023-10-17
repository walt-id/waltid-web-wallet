package id.walt.db.models

import org.jetbrains.exposed.dao.id.UUIDTable

object AccountKeys : UUIDTable("account_keys") {
    val account = reference("account", Accounts)
    val key = reference("key", Keys)
}
