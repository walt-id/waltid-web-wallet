package id.walt.db.models

import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.toKotlinUUID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object WalletCredentials : Table("credentials") {
    val wallet = reference("wallet", Wallets)
    val id = varchar("id", 256)

    val document = text("document")
    val disclosures = text("disclosures").nullable()

    val addedOn = timestamp("added_on")

    override val primaryKey = PrimaryKey(wallet, id)
}

@Serializable
data class WalletCredential(
    val wallet: UUID,
    val id: String,
    val document: String,
    val disclosures: String?,
    val addedOn: Instant,
) {
    constructor(result: ResultRow) : this(
        wallet = result[WalletCredentials.wallet].value.toKotlinUUID(),
        id = result[WalletCredentials.id],
        document = result[WalletCredentials.document],
        disclosures = result[WalletCredentials.disclosures],
        addedOn = result[WalletCredentials.addedOn].toKotlinInstant(),
    )
}
