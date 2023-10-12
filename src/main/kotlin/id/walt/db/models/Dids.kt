package id.walt.db.models

import org.jetbrains.exposed.dao.id.UUIDTable

object Dids : UUIDTable()  {
    val did = varchar("did", 1024).uniqueIndex()
    val document = text("document")
}