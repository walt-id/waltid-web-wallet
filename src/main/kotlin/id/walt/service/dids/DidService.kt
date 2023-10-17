package id.walt.service.dids

import id.walt.db.models.AccountDids
import id.walt.db.models.Accounts
import id.walt.db.models.Dids
import id.walt.db.repositories.AccountDidsRepository
import id.walt.db.repositories.DbAccountDids
import id.walt.db.repositories.DbDid
import id.walt.db.repositories.DidsRepository
import id.walt.service.Did
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object DidsService {
    fun get(account: UUID, did: String): Did? = list(account).singleOrNull { it.did == did }

    fun list(account: UUID): List<Did> = join(account).let {
        AccountDidsRepository.query(it) {
            Did(
                did = it[Dids.did],
                alias = it[AccountDids.alias],
                default = it[AccountDids.default],
                document = it[Dids.document],
            )
        }
    }

    fun add(account: UUID, data: DidInsertDataObject): UUID =
        getOrInsert(data.key, data.did.did, data.did.document).let { did ->
            join(account, data.did.did).let {
                AccountDidsRepository.query(it) {
                    it[AccountDids.id]
                }
            }.takeIf { it.isNotEmpty() }?.single()?.value ?: let {
                AccountDidsRepository.insert(
                    DbAccountDids(
                        account = account, did = did, alias = data.did.alias, default = data.did.default
                    )
                )
            }
        }

    fun delete(account: UUID, did: String): Boolean = join(account, did).let {
        AccountDidsRepository.query(it) {
            it[AccountDids.id]
        }.single().value
    }.let {
        AccountDidsRepository.delete(it)
    }.let { it > 0 }

    fun update(account: UUID, did: DidUpdateDataObject): Boolean = join(account, did.did).let {
        AccountDidsRepository.query(it) {
            it[AccountDids.id]
        }.singleOrNull()?.value
    }?.let {
        when (did) {
            is DidAliasUpdateDataObject -> updateQuery(it, alias = did.alias)
            is DidDefaultUpdateDataObject -> updateQuery(it, isDefault = did.isDefault)
            else -> throw IllegalArgumentException("Unsupported update field: ${did.javaClass.name}")
        }
    }?.let { it > 0 } ?: false

    private fun join(account: UUID, did: String? = null) =
        Accounts.innerJoin(AccountDids, onColumn = { Accounts.id }, otherColumn = { AccountDids.account }).innerJoin(
            Dids,
            onColumn = { Dids.id },
            otherColumn = { AccountDids.did },
            additionalConstraint = did?.let {
                {
                    Dids.did eq did and (Accounts.id eq account)
                }
            }).selectAll()

    private fun find(did: String) = Dids.select { Dids.did eq did }
    private fun getOrInsert(key: UUID, did: String, document: String) = find(did).let {
        DidsRepository.query(it) {
            it[Dids.id]
        }.singleOrNull()?.value
    } ?: let {
        DidsRepository.insert(
            DbDid(
                key = key,
                did = did,
                document = document,
            )
        )
    }

    // TODO: implement in repository
    private fun updateQuery(id: UUID, alias: String? = null, isDefault: Boolean? = null) = transaction {
        AccountDids.update({ AccountDids.id eq id }) { statement ->
            alias?.let { statement[AccountDids.alias] = it }
            isDefault?.let { statement[AccountDids.default] = it }
        }
    }
}