package id.walt.db.models

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import org.jetbrains.exposed.sql.Table

enum class AccountWalletPermissions {
    ADMINISTRATE,
    USE,
    READ_ONLY
}

object AccountWalletMappings : Table("account_wallet_mapping") {
    val account = reference("account", Accounts)
    val wallet = reference("wallet", Wallets)

    val permissions = enumerationByName<AccountWalletPermissions>("permissions", 32)

    override val primaryKey = PrimaryKey(account, wallet)
}

@Serializable
data class AccountWalletListing(
    val account: UUID,
    val wallets: List<WalletPermissionListing>
) {
    @Serializable
    data class WalletPermissionListing(
        val wallet: UUID,
        val permission: AccountWalletPermissions
    )
}

/*
fun main() {
    val uuid1 = UUID.generateUUID()
    val uuid2 = UUID.generateUUID()
    val uuid3 = UUID.generateUUID()
    val uuid4 = UUID.generateUUID()

    println(
        Json.encodeToString(
            AccountWalletListing(
                uuid1,
                listOf(
                    AccountWalletListing.WalletPermissionListing(uuid2, AccountWalletPermissions.ADMINISTRATE),
                    AccountWalletListing.WalletPermissionListing(uuid3, AccountWalletPermissions.USE),
                    AccountWalletListing.WalletPermissionListing(uuid4, AccountWalletPermissions.READ_ONLY)
                )
            )
        )
    )
}*/
