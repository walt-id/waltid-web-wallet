package id.walt.db.repositories

import id.walt.db.models.Credentials
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.util.*

object CredentialsRepository : RepositoryBase<DbCredential>(Credentials) {
    override fun ResultRow.fromRow(): DbCredential = DbCredential(
        id = this[Credentials.id].value,
        credentialId = this[Credentials.credentialId],
        document = this[Credentials.document],
        disclosures = this[Credentials.disclosures]
    )

    override fun DbCredential.toRow(insertStatement: InsertStatement<EntityID<UUID>>): InsertStatement<EntityID<UUID>> =
        let {
            insertStatement[Credentials.credentialId] = it.credentialId
            insertStatement[Credentials.document] = it.document
            insertStatement[Credentials.disclosures] = it.disclosures
            insertStatement
        }
}

data class DbCredential(
    override val id: UUID? = null,
    val credentialId: String,
    val document: String,
    val disclosures: String?
) : DbEntity()
