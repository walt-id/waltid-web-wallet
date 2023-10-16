package id.walt.service.keys

import id.walt.config.ConfigManager
import id.walt.config.DatasourceConfiguration
import id.walt.db.models.AccountKeys
import id.walt.db.models.Accounts
import id.walt.db.models.Keys
import id.walt.db.repositories.AccountKeysRepository
import id.walt.db.repositories.DbAccountKeys
import id.walt.db.repositories.DbKey
import org.jetbrains.exposed.sql.*
import java.util.*

object KeysService {
    fun get(account: UUID, keyId: String): DbKey? = join(account, keyId).let {
        AccountKeysRepository.query(it) {
            DbKey(it[Keys.id].value, it[Keys.keyId], it[Keys.document])
        }.firstOrNull()
    }

    fun list(account: UUID): List<DbKey> = join(account).let {
        AccountKeysRepository.query(it) {
            DbKey(it[Keys.id].value, it[Keys.keyId], it[Keys.document])
        }
    }

    //TODO: remove db entity reference
    fun add(account: UUID, key: DbKey): UUID = join(account, key.keyId).let {
        AccountKeysRepository.query(it) {
            it[AccountKeys.id]
        }.takeIf {
            it.isNotEmpty()
            // account has the key already associated
        }?.first()?.value
        // otherwise, associate key to account
            ?: let {
                AccountKeysRepository.insert(DbAccountKeys(account = account, key = key.id!!))
            }
    }

    fun delete(account: UUID, keyId: String): Boolean = join(account, keyId).let {
        AccountKeysRepository.query(it) {
            it[AccountKeys.id]
        }.firstOrNull()?.value
    }?.let {
        AccountKeysRepository.delete(it)
    }?.let { it > 0 } ?: false

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
                        Keys.keyId eq keyId and (Accounts.id eq account)
                    }
                }).selectAll()
}

fun main() {
    ConfigManager.loadConfigs(emptyArray())
    val datasourceConfig = ConfigManager.getConfig<DatasourceConfiguration>()
    Database.connect(datasourceConfig.hikariDataSource)
//    AccountsService.register(EmailLoginRequest("username", "password"))
//    KeysRepository.insert(DbKey(keyId = "keyId", document = "key-jwk"))
    val key = UUID.fromString("3d20dcc4-0988-4f7a-aff7-28a268c1ccb3")
    val account = UUID.fromString("b4746506-fb50-4f0d-9425-d688999e066c")
    val res = KeysService.get(account, "keyId")
    println(res)
}