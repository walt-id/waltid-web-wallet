package id.walt.service.keri.interfaces

interface KeriOobiInterface {
    fun generate(keystore: String, alias: String, role: String, passcode: String): String;
    fun resolve(keystore: String, passcode: String, oobiAlias: String, url: String): Boolean;
}