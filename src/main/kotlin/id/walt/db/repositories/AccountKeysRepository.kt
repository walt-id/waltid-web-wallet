package id.walt.db.repositories

import id.walt.db.models.AccountKeys
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.util.*

object AccountKeysRepository : RepositoryBase<DbAccountKeys>(AccountKeys) {
    override fun ResultRow.fromRow(): DbAccountKeys = DbAccountKeys(
        id = this[AccountKeys.id].value,
        account = this[AccountKeys.account].value,
        key = this[AccountKeys.key].value,
    )

    override fun DbAccountKeys.toRow(insertStatement: InsertStatement<EntityID<UUID>>): InsertStatement<EntityID<UUID>> =
        let {
            insertStatement[AccountKeys.account] = it.account
            insertStatement[AccountKeys.key] = it.key
            insertStatement
        }
}

data class DbAccountKeys(
    override val id: UUID? = null,
    val account: UUID,
    val key: UUID,
) : DbEntity()