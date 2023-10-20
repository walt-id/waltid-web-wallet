package id.walt.db.repositories

import id.walt.db.models.AccountDids
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.util.*

object AccountDidsRepository : RepositoryBase<DbAccountDids>(AccountDids) {
    override fun ResultRow.fromRow(): DbAccountDids = DbAccountDids(
        id = this[AccountDids.id].value,
        account = this[AccountDids.account].value,
        did = this[AccountDids.did].value,
        alias = this[AccountDids.alias],
        default = this[AccountDids.default],
    )

    override fun DbAccountDids.toRow(insertStatement: InsertStatement<EntityID<UUID>>): InsertStatement<EntityID<UUID>> =
        let {
            insertStatement[AccountDids.account] = it.account
            insertStatement[AccountDids.did] = it.did
            insertStatement[AccountDids.alias] = it.alias
            insertStatement[AccountDids.default] = it.default
            insertStatement
        }
}

data class DbAccountDids(
    override val id: UUID? = null,
    val account: UUID,
    val did: UUID,
    val alias: String,
    val default: Boolean,
) : DbEntity()