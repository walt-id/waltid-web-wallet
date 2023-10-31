package id.walt.service.keri

import id.walt.service.dto.AcdcSaidifyResponse
import id.walt.service.dto.IPEX_EVENT
import id.walt.service.dto.IpexSaid
import id.walt.service.keri.interfaces.IpexInterface
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class IpexService: IpexInterface {
    override fun admit(keystore: String, alias: String, passcode: String, said: String, message: String?) {
        TODO("Not yet implemented")
    }

    override fun agree(): IpexSaid {
        var said: String = ""
        val command: List<String> = listOf(
            "kli", "ipex",
            "agree"
        )

        try {
            val processBuilder = ProcessBuilder(command)
            val process = processBuilder.start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            said= reader.readLine()!!.trimEnd('\n')

            process.waitFor()

        } catch(e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return IpexSaid(said=said)
    }

    override fun apply(): IpexSaid {
        var said: String = ""
        val command: List<String> = listOf(
            "kli", "ipex",
            "apply"
        )

        try {
            val processBuilder = ProcessBuilder(command)
            val process = processBuilder.start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            said= reader.readLine()!!.trimEnd('\n')

            process.waitFor()

        } catch(e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return IpexSaid(said=said)
    }

    override fun grant(
        keystore: String,
        alias: String,
        passcode: String,
        said: String,
        recipient: String,
        message: String?
    ) {
        TODO("Not yet implemented")
    }

    override fun list(
        keystore: String,
        alias: String,
        passcode: String,
        type: IPEX_EVENT,
        verbose: Boolean,
        poll: Boolean,
        sent: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun offer(): IpexSaid {
        var said: String = ""
        val command: List<String> = listOf(
            "kli", "ipex",
            "offer"
        )

        try {
            val processBuilder = ProcessBuilder(command)
            val process = processBuilder.start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            said= reader.readLine()!!.trimEnd('\n')

            process.waitFor()

        } catch(e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return IpexSaid(said=said)
    }

    override fun spurn(keystore: String, alias: String, passcode: String, said: String, message: String?) {
        TODO("Not yet implemented")
    }
}