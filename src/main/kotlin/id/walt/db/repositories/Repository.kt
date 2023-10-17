package id.walt.db.repositories

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

interface Repository<T : DbEntity> {
    fun insert(model: T): UUID
    fun get(id: UUID): T
    fun delete(id: UUID): Int
    fun <K> query(query: Query, distinct: Boolean = true, transform: (ResultRow) -> K): List<K>
}

interface Transformer<T> {
    fun ResultRow.fromRow(): T
    fun T.toRow(insertStatement: InsertStatement<EntityID<UUID>>): InsertStatement<EntityID<UUID>>
}

abstract class RepositoryBase<T : DbEntity>(
    private val table: UUIDTable
) : Repository<T>, Transformer<T> {

    override fun insert(model: T): UUID = transaction {
        table.insertAndGetId { model.toRow(it) }.value
    }

    override fun delete(id: UUID): Int = transaction {
        table.deleteWhere { table.id eq id }
    }

    override fun get(id: UUID): T = transaction {
        table.select { table.id eq id }.single()
    }.fromRow()

    override fun <K> query(query: Query, distinct: Boolean, transform: (ResultRow) -> K): List<K> = transaction {
        query.let { q ->
            distinct.takeIf { it }?.let { q.distinct() } ?: q
        }.map(transform)
    }
}

abstract class DbEntity {
    abstract val id: UUID?
}