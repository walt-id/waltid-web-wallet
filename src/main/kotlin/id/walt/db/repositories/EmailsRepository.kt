package id.walt.db.repositories

import id.walt.db.models.Emails
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.util.*

object EmailsRepository : RepositoryBase<DbEmail>(Emails) {
    override fun ResultRow.fromRow(): DbEmail = DbEmail(
        id = this[Emails.id].value,
        email = this[Emails.email],
        password = this[Emails.password],
    )

    override fun DbEmail.toRow(insertStatement: InsertStatement<EntityID<UUID>>): InsertStatement<EntityID<UUID>> =
        let {
            insertStatement[Emails.email] = it.email
            insertStatement[Emails.password] = it.password
            insertStatement
        }
}

data class DbEmail(
    override val id: UUID? = null,
    val email: String,
    val password: String,
) : DbEntity()