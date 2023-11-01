package id.walt.service

import id.walt.service.nft.NftKitNftService
import id.walt.service.nft.NftService
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object WalletServiceManager {

    private val walletServices = ConcurrentHashMap<UUID, WalletService>()

    fun getWalletService(wallet: UUID): WalletService =
        walletServices.getOrPut(wallet) {
            //WalletKitWalletService(wallet)
            SSIKit2WalletService(wallet)
        }

    fun getNftService(): NftService = NftKitNftService()
}
