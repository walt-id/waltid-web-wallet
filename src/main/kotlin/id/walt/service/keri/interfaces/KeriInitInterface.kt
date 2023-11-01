package id.walt.service.keri.interfaces

import id.walt.service.dto.KeriCreateDbResponse

interface KeriInitInterface {

    fun generateSalt(): String;
    fun createKeystoreDatabase(name: String, passcode: String): KeriCreateDbResponse;
}