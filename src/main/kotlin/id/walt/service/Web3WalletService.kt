package id.walt.service

import id.walt.db.models.AccountWallets
import id.walt.db.models.Wallets
import id.walt.service.dto.ConnectedWalletDataTransferObject
import id.walt.service.dto.WalletDataTransferObject
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object Web3WalletService {
    /**
     * Connects the wallet to the given account
     * @param accountId the account's uuid
     * @param wallet the [WalletDataTransferObject]
     * @return the [ConnectedWalletDataTransferObject] representing the web3 wallet
     */
    fun connect(accountId: UUID, wallet: WalletDataTransferObject): ConnectedWalletDataTransferObject =
        getOrCreateWallet(wallet).let { walletId ->
            assignWallet(accountId, walletId)
            ConnectedWalletDataTransferObject(walletId.toString(), wallet.address, wallet.ecosystem)
        }

    /**
     * Disconnects the wallet from the given account
     * @param accountId the account's uuid
     * @param walletId the wallet's address / public-key
     */
    fun disconnect(accountId: UUID, walletId: UUID): Unit = transaction {
        AccountWallets.deleteWhere { account eq accountId and (wallet eq walletId) }
    }

    /**
     * Fetches the connected wallets for a given account
     * @param accountId the account's uuid
     * @return A list of [ConnectedWalletDataTransferObject]s
     */
    fun getConnected(accountId: UUID) =
        transaction {
            AccountWallets.select { AccountWallets.account eq accountId }.mapNotNull {
                Wallets.select { Wallets.id eq it[AccountWallets.wallet] }.firstOrNull()?.let {
                    ConnectedWalletDataTransferObject(
                        it[Wallets.id].toString(),
                        it[Wallets.address],
                        it[Wallets.ecosystem]
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