package id.walt.service

import id.walt.db.models.AccountWalletMappings
import id.walt.db.models.AccountWalletPermissions
import id.walt.db.models.Wallets
import id.walt.service.account.AccountsService
import id.walt.service.nft.NftKitNftService
import id.walt.service.nft.NftService
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import kotlinx.uuid.UUID
import kotlinx.uuid.toJavaUUID
import kotlinx.uuid.toKotlinUUID
import org.jetbrains.exposed.sql.insert
import java.util.concurrent.ConcurrentHashMap

object WalletServiceManager {

    private val walletServices = ConcurrentHashMap<Pair<UUID, UUID>, WalletService>()

    fun getWalletService(account: UUID, wallet: UUID): WalletService =
        walletServices.getOrPut(Pair(account, wallet)) {
            //WalletKitWalletService(account, wallet)
            SSIKit2WalletService(account, wallet)
        }

    fun createWallet(forAccount: UUID): UUID {
        val accountName = AccountsService.getNameFor(forAccount)

        val walletId = Wallets.insert {
            it[name] = "Wallet of $accountName"
            it[createdOn] = Clock.System.now().toJavaInstant()
        }[Wallets.id].value.toKotlinUUID()

        println("Creating wallet mapping: $forAccount -> $walletId")
        AccountWalletMappings.insert {
            it[account] = forAccount.toJavaUUID()
            it[wallet] = walletId.toJavaUUID()
            it[permissions] = AccountWalletPermissions.ADMINISTRATE
            it[addedOn] = Clock.System.now().toJavaInstant()
        }

        return walletId
    }

    fun getNftService(): NftService = NftKitNftService()
}
