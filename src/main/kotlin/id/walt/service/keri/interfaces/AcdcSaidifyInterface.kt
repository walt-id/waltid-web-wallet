package id.walt.service.keri.interfaces

import id.walt.service.dto.AcdcSaidifyResponse

interface AcdcSaidifyInterface {
    fun saidify(file: String): AcdcSaidifyResponse;
}