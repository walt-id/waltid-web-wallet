package id.walt.db.models

import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.toKotlinUUID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.timestamp

object Accounts : UUIDTable("accounts") {
    val name = varchar("name", 128).nullable()

    val email = varchar("email", 128).nullable().uniqueIndex()
    val password = varchar("password", 200).nullable()

   // val loginWeb3Wallet = uuid("web3wallet").nullable()

    val createdOn = timestamp("createdOn")

    init {
        //foreignKey(id, loginWeb3Wallet, target = Web3Wallets.primaryKey)

        /*check {
            (email.isNotNull() and password.isNotNull()) or loginWeb3Wallet.isNotNull()
        }*/
    }
}

@Serializable
data class Account(
    val id: UUID,
    val name: String? = null,
    val email: String? = null,
    val updatePasswordTo: String? = null,
    //val loginWeb3Wallet: UUID? = null,
    val createdOn: Instant
) {
    constructor(result: ResultRow) : this(
        id = result[Accounts.id].value.toKotlinUUID(),
        name = result[Accounts.name],
        email = result[Accounts.email],
        //loginWeb3Wallet = result[Accounts.loginWeb3Wallet]?.toKotlinUUID(),
        createdOn = result[Accounts.createdOn].toKotlinInstant()
    )
}
