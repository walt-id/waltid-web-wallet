package id.walt.service


import org.junit.Test
import kotlin.test.assertEquals

class TestKeriUtility {
    @Test
    fun testKliVersionCommand() {
        val command = "kli"
        val exitCode = runShellCommand(command)

        assertEquals(0, exitCode, "Command '$command' should exit with code 0")
    }

    private fun runShellCommand(command: String): Int {
        val processBuilder = ProcessBuilder("/bin/bash", "-c", command)
        val process = processBuilder.start()

        // Wait for the command to complete
        return process.waitFor()
    }
}