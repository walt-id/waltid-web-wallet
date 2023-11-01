package id.walt.db.models

import org.jetbrains.exposed.dao.id.UUIDTable

object Dids : UUIDTable("dids") {
    val did = varchar("did", 1024).uniqueIndex()
    val document = text("document")
    val key = reference("key", Keys)
}