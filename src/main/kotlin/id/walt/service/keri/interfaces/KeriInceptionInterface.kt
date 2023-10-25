package id.walt.service.keri.interfaces

import id.walt.service.dto.KeriInceptionResponse

interface KeriInceptionInterface {
    fun inceptController(name: String, alias: String, passcode: String): KeriInceptionResponse;
}