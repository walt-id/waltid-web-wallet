package id.walt.db.repositories

import id.walt.db.models.AccountWallets
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.util.*

object AccountWalletsRepository : RepositoryBase<DbAccountWallets>(AccountWallets) {
    override fun ResultRow.fromRow(): DbAccountWallets = DbAccountWallets(
        id = this[AccountWallets.id].value,
        account = this[AccountWallets.account].value,
        wallet = this[AccountWallets.wallet].value,
        owner = this[AccountWallets.owner],
    )

    override fun DbAccountWallets.toRow(insertStatement: InsertStatement<EntityID<UUID>>): InsertStatement<EntityID<UUID>> =
        let {
            insertStatement[AccountWallets.account] = it.account
            insertStatement[AccountWallets.wallet] = it.wallet
            insertStatement[AccountWallets.owner] = it.owner
            insertStatement
        }
}

data class DbAccountWallets(
    override val id: UUID? = null,
    val account: UUID,
    val wallet: UUID,
    val owner: Boolean,
) : DbEntity()