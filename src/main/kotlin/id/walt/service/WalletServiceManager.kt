package id.walt.service

import id.walt.service.nft.NftKitNftService
import id.walt.service.nft.NftService
import java.util.*
import java.util.concurrent.*

object WalletServiceManager {

    private val walletServices = ConcurrentHashMap<UUID, WalletService>()

    fun getWalletService(wallet: UUID): WalletService =
        walletServices.getOrPut(wallet) {
            WalletKitWalletService(wallet)
        }

    fun getNftService(): NftService = NftKitNftService()
}