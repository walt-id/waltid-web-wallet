package id.walt.service.dids

import id.walt.service.Did
import java.util.*

data class DidInsertDataObject(
    val key: UUID,
    val did: Did
)
