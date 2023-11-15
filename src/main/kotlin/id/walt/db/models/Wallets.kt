package id.walt.db.models

import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.toKotlinUUID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.timestamp

object Wallets : UUIDTable("wallets") {
    val name = varchar("name", 128)
    val createdOn = timestamp("createdOn")
}

@Serializable
data class Wallet(
    val id: UUID,
    val name: String,
    val createdOn: Instant
) {
    constructor(result: ResultRow) : this(
        id = result[Wallets.id].value.toKotlinUUID(),
        name = result[Wallets.name],
        createdOn = result[Wallets.createdOn].toKotlinInstant()
    )
}
