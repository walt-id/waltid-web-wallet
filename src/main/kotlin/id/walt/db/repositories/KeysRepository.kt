package id.walt.db.repositories

import id.walt.db.models.Keys
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.util.*

object KeysRepository : RepositoryBase<DbKey>(Keys) {
    override fun ResultRow.fromRow(): DbKey = DbKey(
        id = this[Keys.id].value,
        keyId = this[Keys.keyId],
        document = this[Keys.document],
    )

    override fun DbKey.toRow(insertStatement: InsertStatement<EntityID<UUID>>): InsertStatement<EntityID<UUID>> = let {
        insertStatement[Keys.keyId] = it.keyId
        insertStatement[Keys.document] = it.document
        insertStatement
    }
}

data class DbKey(
    override val id: UUID? = null,
    val keyId: String,
    val document: String,
) : DbEntity()