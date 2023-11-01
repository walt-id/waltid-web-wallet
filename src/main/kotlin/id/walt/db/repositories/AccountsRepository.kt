package id.walt.db.repositories

import id.walt.db.models.Accounts
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.util.*

object AccountsRepository : RepositoryBase<DbAccount>(Accounts) {
    override fun ResultRow.fromRow(): DbAccount = DbAccount(
        id = this[Accounts.id].value,
        email = this[Accounts.email]?.value,
        wallet = this[Accounts.wallet]?.value,
    )

    override fun DbAccount.toRow(insertStatement: InsertStatement<EntityID<UUID>>): InsertStatement<EntityID<UUID>> =
        let {
            insertStatement[Accounts.email] = it.email
            insertStatement[Accounts.wallet] = it.wallet
            insertStatement
        }
}

data class DbAccount(
    override val id: UUID? = null,
    val email: UUID?,
    val wallet: UUID?,
) : DbEntity()