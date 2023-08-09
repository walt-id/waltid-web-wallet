package id.walt.service

import id.walt.db.models.AccountWallets
import id.walt.db.models.Wallets
import id.walt.service.dto.LinkedWalletDataTransferObject
import id.walt.service.dto.WalletDataTransferObject
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object Web3WalletService {
    /**
     * Adds the wallet to the given account
     * @param accountId the account's uuid
     * @param wallet the [WalletDataTransferObject]
     * @return the [LinkedWalletDataTransferObject] representing the web3 wallet
     */
    fun link(accountId: UUID, wallet: WalletDataTransferObject): LinkedWalletDataTransferObject =
        getOrCreateWallet(wallet).let { walletId ->
            assignWallet(accountId, walletId)
            LinkedWalletDataTransferObject(walletId.toString(), wallet.address, wallet.ecosystem, false)
        }

    /**
     * Removes the wallet from the given account
     * @param accountId the account's [UUID]
     * @param walletId the wallet's [UUID]
     */
    fun unlink(accountId: UUID, walletId: UUID): Unit = transaction {
        AccountWallets.deleteWhere { account eq accountId and (wallet eq walletId) }
    }

    /**
     * Resets the owner property for the given account
     * @param accountId the account's [UUID]
     * @param walletId the wallet's [UUID]
     */
    fun disconnect(accountId: UUID, walletId: UUID): Unit = transaction {
        AccountWallets.update(
            { AccountWallets.account eq accountId and (AccountWallets.wallet eq walletId) }
        ) {
            it[AccountWallets.owner] = false
        }
    }

    /**
     * Fetches the wallets for a given account
     * @param accountId the account's [UUID]
     * @return A list of [LinkedWalletDataTransferObject]s
     */
    fun getLinked(accountId: UUID) =
        transaction {
            AccountWallets.select { AccountWallets.account eq accountId }.mapNotNull { aw ->
                Wallets.select { Wallets.id eq aw[AccountWallets.wallet] }.firstOrNull()?.let { w ->
                    LinkedWalletDataTransferObject(
                        w[Wallets.id].toString(),
                        w[Wallets.address],
                        w[Wallets.ecosystem],
                        aw[AccountWallets.owner]
                    )
                }
            }
        }

    private fun getOrCreateWallet(wallet: WalletDataTransferObject) = transaction {
        Wallets.select { Wallets.address eq wallet.address }.firstOrNull()?.let { it[Wallets.id].value }
    } ?: transaction {
        Wallets.insertAndGetId {
            it[address] = wallet.address
            it[ecosystem] = wallet.ecosystem
        }.value
    }

    private fun assignWallet(accountId: UUID, walletId: UUID) = transaction {
        AccountWallets.select {
            AccountWallets.account eq accountId and (AccountWallets.wallet eq walletId)
        }.takeIf { it.any() }?.let { it.first()[AccountWallets.id].value } ?: let {
            AccountWallets.insertAndGetId {
                it[account] = accountId
                it[wallet] = walletId
            }.value
        }
    }
}