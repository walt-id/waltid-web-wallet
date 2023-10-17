package id.walt.db.repositories

import id.walt.db.models.Dids
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.util.*

object DidsRepository : RepositoryBase<DbDid>(Dids) {
    override fun ResultRow.fromRow(): DbDid = DbDid(
        id = this[Dids.id].value,
        key = this[Dids.key].value,
        did = this[Dids.did],
        document = this[Dids.document]
    )

    override fun DbDid.toRow(insertStatement: InsertStatement<EntityID<UUID>>): InsertStatement<EntityID<UUID>> = let {
        insertStatement[Dids.did] = it.did
        insertStatement[Dids.document] = it.document
        insertStatement[Dids.key] = it.key
        insertStatement
    }
}

data class DbDid(
    override val id: UUID? = null,
    val key: UUID,
    val did: String,
    val document: String,
) : DbEntity()