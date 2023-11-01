package id.walt.service.keri.interfaces

interface AcdcManagementInterface {
    fun create(keystore: String,
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
               time: String)

    /**
     * Send credential presentation for specified credential to recipient
     */
    fun present(keystore: String,
                alias: String,
                passcode: String,
                said: String,
                recipient: String)

    /**
     * List credentials and check mailboxes for any newly issued credentials
     */
    fun list(keystore: String,
             alias: String,
             passcode: String,
             verbose: Boolean,
             poll: Boolean,
             issued: Boolean,
             said: Boolean,
             schema: Boolean): String

    fun revoke(keystore: String,
               alias: String,
               registry: String,
               passcode: String,
               said: String,
               send: String?,
               time: String?)
}