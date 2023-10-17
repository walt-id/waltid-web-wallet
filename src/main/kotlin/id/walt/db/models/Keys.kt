package id.walt.db.models

import org.jetbrains.exposed.dao.id.UUIDTable

object Keys : UUIDTable("keys")  {
    val keyId = varchar("kid", 512).uniqueIndex()
    val document = text("document")
}