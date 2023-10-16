package id.walt.service.dids

abstract class DidUpdateDataObject(
    open val did: String,
)

data class DidDefaultUpdateDataObject(
    override val did: String,
    val isDefault: Boolean,
) : DidUpdateDataObject(did)

data class DidAliasUpdateDataObject(
    override val did: String,
    val alias: String,
) : DidUpdateDataObject(did)