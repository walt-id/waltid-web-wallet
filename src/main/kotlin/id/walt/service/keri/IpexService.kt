package id.walt.service.keri

import id.walt.service.dto.IPEX_EVENT
import id.walt.service.dto.IpexSaid
import id.walt.service.keri.interfaces.IpexInterface
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class IpexService: IpexInterface {
    override fun admit(keystore: String, alias: String, passcode: String, said: String, message: String?): String {
        var result: String = ""
        val command: MutableList<String> = mutableListOf(
            "kli", "ipex",
            "admit",
            "--name", keystore,
            "--alias", alias,
            "--passcode", passcode,
            "--said", said
        )

        if (message != null) {
            command.add("--message"); command.add(message)
        }

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