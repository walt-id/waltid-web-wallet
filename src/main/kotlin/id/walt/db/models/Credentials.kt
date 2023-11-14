package id.walt.db.models

import org.jetbrains.exposed.dao.id.UUIDTable

object Credentials : UUIDTable("credentials")  {
    val credentialId = varchar("cid", 256).uniqueIndex()
    val document = text("document")
    val disclosures = text("disclosures").nullable()
}
