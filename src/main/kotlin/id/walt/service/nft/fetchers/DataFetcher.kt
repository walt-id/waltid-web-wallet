package id.walt.service.nft.fetchers

import id.walt.service.dto.NftDetailDataTransferObject
import id.walt.service.nft.converters.EvmDetailConverter
import id.walt.service.nft.converters.FlowDetailConverter
import id.walt.service.nft.converters.NearDetailConverter
import id.walt.service.nft.converters.TezosDetailConverter
import id.walt.service.nft.fetchers.parameters.TokenDetailParameter
import id.walt.service.nft.fetchers.parameters.TokenListParameter

interface DataFetcher {
    fun all(parameter: TokenListParameter): List<NftDetailDataTransferObject>
    fun byId(parameter: TokenDetailParameter): NftDetailDataTransferObject

    companion object {
        private val evmFetcher = EvmDataFetcher(EvmDetailConverter())
        private val tezosFetcher = TezosDataFetcher(TezosDetailConverter())
        private val nearFetcher = NearDataFetcher(NearDetailConverter())
        private val flowFetcher = FlowDataFetcher(FlowDetailConverter())

        fun select(ecosystem: String) = when (ecosystem) {
            "ethereum" -> evmFetcher
            "tezos" -> tezosFetcher
            "near" -> nearFetcher
            "flow" -> flowFetcher
            else -> throw IllegalArgumentException("Ecosystem $ecosystem not supported.")
        }
    }
}
