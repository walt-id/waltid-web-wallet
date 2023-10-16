package id.walt.db.repositories

import id.walt.db.models.Wallets
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.util.*

object WalletsRepository : RepositoryBase<DbWallet>(Wallets) {
    override fun ResultRow.fromRow(): DbWallet = DbWallet(
        id = this[Wallets.id].value,
        address = this[Wallets.address],
        ecosystem = this[Wallets.ecosystem],
    )

    override fun DbWallet.toRow(insertStatement: InsertStatement<EntityID<UUID>>): InsertStatement<EntityID<UUID>> =
        let {
            insertStatement[Wallets.address] = it.address
            insertStatement[Wallets.ecosystem] = it.ecosystem
            insertStatement
        }
}

data class DbWallet(
    override val id: UUID? = null,
    val address: String,
    val ecosystem: String,
) : DbEntity()