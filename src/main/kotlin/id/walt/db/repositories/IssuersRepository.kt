package id.walt.db.repositories

import id.walt.db.models.Issuers
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.util.*

object IssuersRepository : RepositoryBase<DbIssuer>(Issuers) {
    override fun ResultRow.fromRow(): DbIssuer = DbIssuer(
        id = this[Issuers.id].value,
        name = this[Issuers.name],
        description = this[Issuers.description],
        uiEndpoint = this[Issuers.uiEndpoint],
        configurationEndpoint = this[Issuers.configurationEndpoint],
    )

    override fun DbIssuer.toRow(insertStatement: InsertStatement<EntityID<UUID>>): InsertStatement<EntityID<UUID>> =
        let {
            insertStatement[Issuers.name] = it.name
            insertStatement[Issuers.description] = it.description
            insertStatement[Issuers.uiEndpoint] = it.uiEndpoint
            insertStatement[Issuers.configurationEndpoint] = it.configurationEndpoint
            insertStatement
        }
}

data class DbIssuer(
    override val id: UUID? = null,
    val name: String,
    val description: String? = null,
    val uiEndpoint: String,
    val configurationEndpoint: String,
) : DbEntity()