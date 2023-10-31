package id.walt.service.keri

import id.walt.service.dto.KeriCreateDbResponse
import id.walt.service.keri.interfaces.KeriOobiInterface
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class KeriOobiService: KeriOobiInterface {
    override fun generate(keystore: String, alias: String, role: String, passcode: String): String {
        TODO("Not yet implemented")
    }

    override fun resolve(keystore: String, passcode: String, oobiAlias: String, url: String): Boolean {
        var status = 0
        val command: List<String> = listOf(
            "kli", "oobi",
            "resolve",
            "--name", keystore,
            "--passcode", passcode,
            "--oobi-alias", oobiAlias,
            "--oobi", url
        )

        try {
            val processBuilder = ProcessBuilder(command)
            val process = processBuilder.start()

            status = process.waitFor()

        } catch(e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        return status == 0
    }
}