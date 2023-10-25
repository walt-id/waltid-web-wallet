package id.walt.service.keri

import id.walt.service.dto.KeriInceptionResponse
import id.walt.service.keri.interfaces.KeriInceptionInterface
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class KeriInceptionService: KeriInceptionInterface {

    private val CONFIG_DIR = "config"
    private val INCEPTION_FILE = "inception-config.json"
    override fun inceptController(name: String, alias: String, passcode: String): KeriInceptionResponse {
        var prefix = ""
        val publicKeys = mutableListOf<String>()

        val inceptCommand: List<String> = listOf(
            "kli", "incept",
            "--name" ,name,
            "--alias", alias,
            "--passcode", passcode,
            "--file", "$CONFIG_DIR/keri/$INCEPTION_FILE"
        )

        // kli status --name waltid --alias waltid-alias | awk '/Identifier:/ {print $2}'
        val statusCommand: List<String> = listOf(
            "kli", "status",
            "--name" ,name,
            "--alias", alias,
            "--passcode", passcode
        )

        try {
            val processBuilder = ProcessBuilder(inceptCommand)
            val process =  processBuilder.start()
            process.waitFor()

            val statusProcessBuilder = ProcessBuilder(statusCommand)
            val statusProcess = statusProcessBuilder.start()

            val prefixInputReader = BufferedReader(InputStreamReader(statusProcess.inputStream))

            var line: String?
            var capturingPublicKeys = false
            while (prefixInputReader.readLine().also { line = it } != null) {
                if (line?.contains("Identifier:") == true) {
                    prefix = line!!.split(" ")[1]
                }

                if (line?.startsWith("Public Keys:") == true) {
                    capturingPublicKeys = true
                    continue
                }

                if (capturingPublicKeys && line?.isNotBlank() == true) {
                    publicKeys.add(line!!.trim().split(" ")[1])
                } else if (capturingPublicKeys && line?.isBlank() == true) {
                    break
                }
            }

            statusProcess.waitFor()

        } catch(e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        return KeriInceptionResponse(prefix, "did:keri:$prefix", publicKeys)
    }

}