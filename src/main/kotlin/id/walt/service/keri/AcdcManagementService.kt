package id.walt.service.keri

import id.walt.service.keri.interfaces.AcdcManagementInterface

class AcdcManagementService: AcdcManagementInterface {
    override fun create(
        keystore: String,
        registry: String,
        passcode: String,
        schema: String,
        edges: String,
        rules: String,
        recipient: String,
        data: String,
        credential: String,
        alias: String,
        private: Boolean,
        time: String
    ) {
        TODO("Not yet implemented")
    }

    override fun present(keystore: String, alias: String, passcode: String, said: String, recipient: String) {
        TODO("Not yet implemented")
    }

    override fun list(
        keystore: String,
        alias: String,
        passcode: String,
        verbose: Boolean,
        poll: Boolean,
        issued: Boolean,
        said: Boolean,
        schema: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun revoke(
        keystore: String,
        alias: String,
        registry: String,
        passcode: String,
        said: String,
        send: String,
        time: String
    ) {
        TODO("Not yet implemented")
    }
}