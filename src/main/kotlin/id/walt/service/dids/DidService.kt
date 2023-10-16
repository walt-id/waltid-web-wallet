package id.walt.service.dids

import id.walt.db.models.*
import id.walt.db.repositories.*
import id.walt.service.Did
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object DidsService {
    fun get(account: UUID, did: String): Did {
        TODO()
    }

    fun list(account: UUID): List<Did> {
        TODO()
    }

    fun add(account: UUID, data: DidInsertDataObject): DbDid {
        transaction {
            Accounts.innerJoin(AccountKeys, onColumn = { Accounts.id }, otherColumn = { AccountKeys.account })
                .innerJoin(Keys, onColumn = { Keys.id }, otherColumn = { AccountKeys.key }).selectAll().firstOrNull()
                ?.let {

                }
        }

        //TODO: check if not already exists
        val akid = AccountKeysRepository.insert(DbAccountKeys(account = account, key = data.key))
        val ddid = DidsRepository.insert(DbDid(key = data.key, did = data.did.did, document = data.did.document))
        val adid = AccountDidsRepository.insert(DbAccountDids(account = account, did = ddid, alias = data.did.alias, default = data.did.default))
        TODO()
    }

    fun delete(account: UUID, did: String): Boolean {
        TODO()
    }

    fun update(account: UUID, did: DidUpdateDataObject): Boolean{

        TODO()
    }
}