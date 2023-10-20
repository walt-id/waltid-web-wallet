package id.walt.db.models

import org.jetbrains.exposed.dao.id.UUIDTable

object Issuers : UUIDTable("issuers") {
    val name = varchar("name", 128).uniqueIndex()
    val description = text("description").nullable().default("no description")
    val uiEndpoint = varchar("ui", 128)
    val configurationEndpoint = varchar("configuration", 256)
}