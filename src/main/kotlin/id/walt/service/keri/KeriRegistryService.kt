package id.walt.service.keri

import id.walt.service.dto.KeriRegistryInceptionResponse
import id.walt.service.keri.interfaces.KeriRegistryInterface
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class KeriRegistryService: KeriRegistryInterface {
    override fun incept(keystore: String, alias: String, registry: String, passcode: String): KeriRegistryInceptionResponse {
        var scid = ""

        val command: List<String> = listOf(
            "kli", "vc",
            "registry", "incept",
            "--name", keystore,
            "--alias", alias,
            "--registry-name", registry,
            "--passcode", passcode
        )

        try {
            val processBuilder = ProcessBuilder(command)
            val process = processBuilder.start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                if(line!!.startsWith("Registry: ")) {
                    val index: Int = line!!.indexOf('(')
                    scid = line!!.substring(index + 1, line!!.length - 2)
                }
            }

            val errorReader = BufferedReader(InputStreamReader(process.errorStream))
            var errorLine: String?
            while (errorReader.readLine().also { errorLine = it } != null) {
                System.err.println(errorLine)
            }
            process.waitFor()

        } catch(e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        return KeriRegistryInceptionResponse(scid)
    }
}