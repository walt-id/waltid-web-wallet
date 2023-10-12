package id.walt.service.keys

import id.walt.db.models.AccountKeys
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

//TODO: introduce and implement db-entities
object KeysService {

    fun getById(account: UUID, id: String): String? = transaction {
        AccountKeys.select { AccountKeys.account eq account and (AccountKeys.keyId eq id) }.firstOrNull()?.let {
            it[AccountKeys.document]
        }
    }

    fun getAllForAccount(account: UUID) = transaction {
        AccountKeys.select { (AccountKeys.account eq account) }.map {
            Pair(it[AccountKeys.keyId], it[AccountKeys.document])
        }
    }

    fun insert(account: UUID, keyId: String, document: String) = transaction {
        AccountKeys.insert {
            it[AccountKeys.account] = account
            it[AccountKeys.keyId] = keyId
            it[AccountKeys.document] = document
        }.insertedCount == 1
    }
}