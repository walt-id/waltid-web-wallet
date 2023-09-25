package id.walt.db.models

import id.walt.core.crypto.keys.Key
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.json.json

object WalletKeys : Table() {
    val wallet = reference("wallet", Wallets.id)
    val keyId = varchar("kid", 512)
    val document = json<Key>("key", Json)

    override val primaryKey = PrimaryKey(wallet, keyId)
}
