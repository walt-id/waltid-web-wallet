package id.walt.db.models

import org.jetbrains.exposed.dao.id.UUIDTable

object Wallets : UUIDTable("\"wallets\"") {
    val address = varchar("address", 256).uniqueIndex()
    val ecosystem = varchar("ecosystem", 128)
}