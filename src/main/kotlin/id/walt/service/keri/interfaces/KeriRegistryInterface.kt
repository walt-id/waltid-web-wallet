package id.walt.service.keri.interfaces

import id.walt.service.dto.KeriRegistryInceptionResponse

interface KeriRegistryInterface {
    fun incept(keystore: String, alias: String, registry: String, passcode: String): KeriRegistryInceptionResponse;
}