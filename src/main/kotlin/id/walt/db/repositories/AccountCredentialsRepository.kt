package id.walt.db.repositories

import id.walt.db.models.AccountCredentials
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.util.*

object AccountCredentialsRepository : RepositoryBase<DbAccountCredentials>(AccountCredentials) {
    override fun ResultRow.fromRow(): DbAccountCredentials = DbAccountCredentials(
        id = this[AccountCredentials.id].value,
        account = this[AccountCredentials.account].value,
        credential = this[AccountCredentials.credential].value,
    )

    override fun DbAccountCredentials.toRow(insertStatement: InsertStatement<EntityID<UUID>>): InsertStatement<EntityID<UUID>> =
        let {
            insertStatement[AccountCredentials.account] = it.account
            insertStatement[AccountCredentials.credential] = it.credential
            insertStatement
        }
}

data class DbAccountCredentials(
    override val id: UUID? = null,
    val account: UUID,
    val credential: UUID,
) : DbEntity()