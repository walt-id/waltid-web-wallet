package id.walt.service.keri

import id.walt.service.dto.IPEX_EVENT
import id.walt.service.dto.IpexSaid
import id.walt.service.keri.interfaces.IpexInterface
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class IpexService: IpexInterface {

    // GRANT=$(kli ipex list --name holder --alias holder --poll --said)
    // kli ipex admit --name holder --alias holder --said "${GRANT}"
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

    // kli ipex grant --name issuer --alias issuer --said "${SAID}" --recipient ELjSFdrTdCebJlmvbFNX9-TLhR2PO0_60al1kQp5_e6k
    override fun grant(
        keystore: String,
        alias: String,
        passcode: String,
        said: String,
        recipient: String,
        message: String?
    ): IpexSaid {
        var result = ""
        val command: List<String> = listOf(
            "kli", "ipex",
            "grant",
            "--name", keystore,
            "--alias", alias,
            "--passcode", passcode,
            "--said", said,
            "--recipient", recipient
        )
        try {
            val processBuilder = ProcessBuilder(command)
            val process = processBuilder.start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            result = reader.readLine()!!.trimEnd('\n')

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


        return IpexSaid(said=result)
    }

    // GRANT=$(kli ipex list --name holder --alias holder --poll --said)
    override fun list(
        keystore: String,
        alias: String,
        passcode: String,
        schema: String,
        type: IPEX_EVENT?,
        verbose: Boolean,
        poll: Boolean,
        sent: Boolean,
        said: Boolean
    ): IpexSaid {
        var result = ""

        val command: MutableList<String> = mutableListOf(
            "kli", "ipex",
            "grant",
            "--name", keystore,
            "--alias", alias,
            "--passcode", passcode,
            "--schema", schema
        )

        command.takeIf { poll }?.add("--poll")
        command.takeIf { sent }?.add("--sent")
        command.takeIf { said }?.add("--said")

        try {
            val processBuilder = ProcessBuilder(command)
            val process = processBuilder.start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            result= reader.readLine()!!.trimEnd('\n')

            process.waitFor()

        } catch(e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return IpexSaid(said=result)
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