package id.walt.db.repositories

import id.walt.db.models.AccountIssuers
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.util.*

object AccountIssuersRepository : RepositoryBase<DbAccountIssuers>(AccountIssuers) {
    override fun ResultRow.fromRow(): DbAccountIssuers = DbAccountIssuers(
        id = this[AccountIssuers.id].value,
        account = this[AccountIssuers.account].value,
        issuer = this[AccountIssuers.issuer].value,
    )

    override fun DbAccountIssuers.toRow(insertStatement: InsertStatement<EntityID<UUID>>): InsertStatement<EntityID<UUID>> =
        let {
            insertStatement[AccountIssuers.account] = it.account
            insertStatement[AccountIssuers.issuer] = it.issuer
            insertStatement
        }
}

data class DbAccountIssuers(
    override val id: UUID? = null,
    val account: UUID,
    val issuer: UUID,
) : DbEntity()
