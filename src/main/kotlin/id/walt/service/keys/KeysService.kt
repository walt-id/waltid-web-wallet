package id.walt.service.keys

import id.walt.db.models.AccountKeys
import id.walt.db.models.Accounts
import id.walt.db.models.Keys
import id.walt.db.repositories.AccountKeysRepository
import id.walt.db.repositories.DbAccountKeys
import id.walt.db.repositories.DbKey
import id.walt.db.repositories.KeysRepository
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import java.util.*

object KeysService {
    fun get(account: UUID, keyId: String): DbKey? = list(account).singleOrNull { it.keyId == keyId }

    fun list(account: UUID): List<DbKey> = join(account).let {
        AccountKeysRepository.query(it) {
            DbKey(it[Keys.id].value, it[Keys.keyId], it[Keys.document])
        }
    }

    //TODO: remove db entity reference
    fun add(account: UUID, key: DbKey): UUID = getOrInsert(key.keyId, key.document).let { kid ->
        join(account, key.keyId).let {
            AccountKeysRepository.query(it) {
                it[Keys.id]
            }.takeIf {
                it.isNotEmpty()
                // account has the key already associated
            }?.single()?.value
            // otherwise, associate the key to account
                ?: let {
                    AccountKeysRepository.insert(DbAccountKeys(account = account, key = kid))
                    kid
                }
        }
    }

    fun delete(account: UUID, keyId: String): Boolean = join(account, keyId).let {
        AccountKeysRepository.query(it) {
            it[AccountKeys.id]
        }.single().value
    }.let {
        AccountKeysRepository.delete(it)
    }.let { it > 0 }

    fun update(account: UUID, key: DbKey): Boolean {
        TODO()
    }

    private fun join(account: UUID, keyId: String? = null) =
        Accounts.innerJoin(AccountKeys, onColumn = { Accounts.id }, otherColumn = { AccountKeys.account }).innerJoin(
                Keys,
                onColumn = { Keys.id },
                otherColumn = { AccountKeys.key },
                additionalConstraint = keyId?.let {
                    {
                        Keys.keyId eq keyId
                    }
                }).selectAll()

    private fun find(keyId: String) = Keys.select { Keys.keyId eq keyId }
    private fun getOrInsert(keyId: String, document: String) = find(keyId).let {
        KeysRepository.query(it) {
            it[Keys.id]
        }.singleOrNull()?.value
    } ?: let {
        KeysRepository.insert(
            DbKey(
                keyId = keyId,
                document = document,
            )
        )
    }
}