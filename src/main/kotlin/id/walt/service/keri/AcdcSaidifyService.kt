package id.walt.service.keri

import id.walt.service.dto.AcdcSaidifyResponse
import id.walt.service.keri.interfaces.AcdcSaidifyInterface
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.File

class AcdcSaidifyService: AcdcSaidifyInterface {
    override fun saidify(file: String): AcdcSaidifyResponse {
        var said = "";
        var content = ""

        val command: List<String> = listOf(
            "kli", "saidify",
            "--file", file
        )

        try {
            val processBuilder = ProcessBuilder(command)
            val process = processBuilder.start()

            val errorReader = BufferedReader(InputStreamReader(process.errorStream))
            var errorLine: String?
            while (errorReader.readLine().also { errorLine = it } != null) {
                System.err.println(errorLine)
            }

            process.waitFor()

            content = readFileContent(file)
            said = extractSAID(file)!!
            println("####################")
            println(said)
            println("####################")

        } catch(e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return AcdcSaidifyResponse(said=said, file=content)
    }

    private fun readFileContent(fileName: String): String {
        try {
            val file = File(fileName)
            if (file.exists()) {
                return file.readText()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    private fun extractSAID(fileName: String): String? {
        val pattern = "\"d\":\\s*\"([^\"]+)\"".toRegex()
        try {
            val file = File(fileName)
            if (file.exists()) {
                val fileContent = file.readText()
                val matchResult = pattern.find(fileContent)

                return matchResult?.groupValues?.getOrNull(1)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}