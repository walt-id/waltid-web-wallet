package id.walt.service.keri

import id.walt.service.dto.KeriCreateDbResponse
import id.walt.service.keri.interfaces.KeriInitInterface
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class KeriInitService: KeriInitInterface {

    private val CONFIG_DIR = "config"
    private val OOBI_FILE = "controller-oobi-bootstrap.json"
    override fun generateSalt(): String {
        val command: List<String> = listOf("kli", "salt")
        var salt: String = ""
        try {
            val processBuilder = ProcessBuilder(command)
            val process = processBuilder.start()


            val reader = BufferedReader(InputStreamReader(process.inputStream))

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                salt += line
            }

            val errorReader = BufferedReader(InputStreamReader(process.errorStream))
            var errorLine: String?
            while (errorReader.readLine().also { errorLine = it } != null) {
                System.err.println(errorLine)
            }
            process.waitFor()

        } catch(e: IOException ) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        if (salt.isBlank()) {
            throw IllegalArgumentException("KERI: Salt could not be generated")
        } else {
            return salt.trimEnd('\n')
        }
    }


    override fun createKeystoreDatabase(name: String, passcode: String): KeriCreateDbResponse {
        val salt: String = generateSalt()
        val witnessesOOBIs = mutableListOf<String>()

        val command: List<String> = listOf(
            "kli", "init",
            "--name" ,name,
            "--salt", salt,
            "--passcode", passcode,
            "--config-dir", CONFIG_DIR,
            "--config-file", OOBI_FILE
        )

        try {
            val processBuilder = ProcessBuilder(command)
            val process = processBuilder.start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                println(line)
                if(line!!.startsWith("http")) {
                    witnessesOOBIs.add(line!!.split(" ")[0])
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

        return KeriCreateDbResponse(salt, witnessesOOBIs)
    }
}