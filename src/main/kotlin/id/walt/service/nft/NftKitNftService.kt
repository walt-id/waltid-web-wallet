package id.walt.service.nft

import id.walt.config.ChainExplorerConfiguration
import id.walt.config.ConfigManager
import id.walt.config.MarketPlaceConfiguration
import id.walt.db.models.Wallets
import id.walt.nftkit.services.*
import id.walt.service.dto.*
import id.walt.service.nft.fetchers.DataFetcher
import id.walt.service.nft.fetchers.parameters.*
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class NftKitNftService : NftService {

    override suspend fun getChains(ecosystem: String): List<ChainDataTransferObject> = when (ecosystem.lowercase()) {
        "ethereum" -> EVMChain.values()
        "tezos" -> TezosChain.values()
        "flow" -> FlowChain.values()
        "near" -> NearChain.values()
        "polkadot" -> PolkadotParachain.values()
        "algorand" -> AlgorandChain.values()

        else -> null
    }?.let {
        it.map { ChainDataTransferObject(it.name.uppercase(), ecosystem) }
    } ?: emptyList()

    override suspend fun getMarketPlace(parameter: TokenMarketPlaceParameter): MarketPlaceDataTransferObject? =
        ConfigManager.getConfig<MarketPlaceConfiguration>().let {
            it.marketplaces.firstOrNull {
                it.chain == parameter.chain.lowercase()
            }?.let {
                MarketPlaceDataTransferObject(
                    name = it.name,
                    url = String.format(it.url, parameter.contract, parameter.tokenId)
                )
            }
        }

    override suspend fun getExplorer(parameter: ChainExplorerParameter): ChainExplorerDataTransferObject? =
        ConfigManager.getConfig<ChainExplorerConfiguration>().let {
            it.explorers.firstOrNull {
                it.chain == parameter.chain.lowercase()
            }?.let {
                ChainExplorerDataTransferObject(String.format(it.url, parameter.contract))
            }
        }

    override suspend fun filterTokens(parameter: FilterParameter): List<FilterNftDataTransferObject> =
        parameter.ids.mapNotNull { id ->
            getWalletById(id)?.let { wallet -> //TODO: fix double conversion to wallet (here + inside getTokens)
                getChains(wallet.ecosystem).filter { net ->
                    parameter.networks.takeIf { it.isNotEmpty() }?.contains(net.network) ?: true//TODO: fix repeated conversion
                }.flatMap {
                    getTokens(ListFetchParameter(chain = it.network, walletId = id))
                }.let {
                    FilterNftDataTransferObject(tokens = it, owner = wallet)
                }
            }
        }

    override suspend fun getTokens(parameter: ListFetchParameter): List<NftDetailDataTransferObject> =
        getWalletById(parameter.walletId)?.let {
            DataFetcher.select(it.ecosystem).all(TokenListParameter(chain = parameter.chain, accountId = it.address))
        } ?: emptyList()

    override suspend fun getTokenDetails(parameter: DetailFetchParameter): NftDetailDataTransferObject =
        getWalletById(parameter.walletId)?.let {
            DataFetcher.select(it.ecosystem).byId(
                TokenDetailParameter(
                    chain = parameter.chain,
                    accountId = it.address,
                    contract = parameter.contract,
                    tokenId = parameter.tokenId,
                    collectionId = parameter.collectionId,
                )
            )
        } ?: throw IllegalArgumentException("Token details not available: $parameter")

    private fun getWalletById(accountId: String) = getWalletById(UUID.fromString(accountId))

    private fun getWalletById(accountId: UUID) =
        transaction { Wallets.select { Wallets.id eq accountId }.firstOrNull() }?.let {
            WalletDataTransferObject(address = it[Wallets.address], ecosystem = it[Wallets.ecosystem])
        }
}
