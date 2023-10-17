package id.walt.db.repositories

import id.walt.db.models.WalletOperationHistories
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.time.Instant
import java.util.*

object WalletOperationHistoriesRepository : RepositoryBase<DbWalletOperationHistory>(WalletOperationHistories) {
    override fun ResultRow.fromRow(): DbWalletOperationHistory = DbWalletOperationHistory(
        id = this[WalletOperationHistories.id].value,
        accountId = this[WalletOperationHistories.account].value,
        timestamp = this[WalletOperationHistories.timestamp],
        operation = this[WalletOperationHistories.operation],
        data = this[WalletOperationHistories.data],
    )

    override fun DbWalletOperationHistory.toRow(insertStatement: InsertStatement<EntityID<UUID>>): InsertStatement<EntityID<UUID>> =
        let {
            insertStatement[WalletOperationHistories.account] = it.accountId
            insertStatement[WalletOperationHistories.timestamp] = it.timestamp
            insertStatement[WalletOperationHistories.operation] = it.operation
            insertStatement[WalletOperationHistories.data] = it.data
            insertStatement
        }
}

data class DbWalletOperationHistory(
    override val id: UUID? = null,
    val accountId: UUID,
    val timestamp: Instant,
    val operation: String,
    val data: String,
) : DbEntity()