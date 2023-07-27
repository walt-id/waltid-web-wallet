package id.walt.service.nft.converters

import id.walt.nftkit.services.NearNftMetadata
import id.walt.service.dto.NftConvertResult
import id.walt.service.dto.TokenArt

class NearDetailConverter : NftDetailConverterBase<NearNftMetadata>() {
    override fun convert(data: NearNftMetadata): NftConvertResult = NftConvertResult(
        id = data.token_id,
        name = data.metadata.title,
//        contract = route.params.id.split(":")[0],TODO
        description = data.metadata.description,
        art = TokenArt(url = data.metadata.media),
    )
}
