package id.walt.service.keri

import id.walt.service.keri.interfaces.AcdcManagementInterface
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

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
    ): String {
        var result = ""
        val command: MutableList<String> = mutableListOf(
            "kli", "vc",
            "list",
            "--name", keystore,
            "--alias", alias,
            "--passcode", passcode
        )

        command.takeIf { verbose }?.add("--verbose")
        command.takeIf { poll }?.add("--poll")
        command.takeIf { issued }?.add("--issued")
        command.takeIf { said }?.add("--said")
        command.takeIf { schema }?.add("--schema")

        try {
            val processBuilder = ProcessBuilder(command)
            val process = processBuilder.start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                result+=line
            }

            process.waitFor()

        } catch(e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return result
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