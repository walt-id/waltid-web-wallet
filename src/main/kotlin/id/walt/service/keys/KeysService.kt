package id.walt.service.keys

import id.walt.db.models.WalletKeys
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

//TODO: introduce and implement db-entities
object KeysService {

    fun getById(account: UUID, id: String): String? = transaction {
        WalletKeys.select { WalletKeys.account eq account and (WalletKeys.keyId eq id) }.firstOrNull()?.let {
            it[WalletKeys.document]
        }
    }

    fun getAllForAccount(account: UUID) = transaction {
        WalletKeys.select { (WalletKeys.account eq account) }.map {
            Pair(it[WalletKeys.keyId], it[WalletKeys.document])
        }
    }

    fun insert(account: UUID, keyId: String, document: String) = transaction {
        WalletKeys.insert {
            it[WalletKeys.account] = account
            it[WalletKeys.keyId] = keyId
            it[WalletKeys.document] = document
        }.insertedCount == 1
    }
}