package config.keri.acdc.vlei

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.test.assertTrue
import java.net.InetSocketAddress
import java.net.Socket
import org.junit.Test
class ServerTestvLEI {
    @Test
    fun testServerAvailability() = runBlocking {
        val host = "localhost"
        val port = 7723

        val serverAvailable = async(Dispatchers.IO) {
            isServerAvailable(host, port)
        }

        assertTrue(serverAvailable.await(), "The server on $host:$port is not available.")
    }

    private fun isServerAvailable(host: String, port: Int): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), 2000)
                true
            }
        } catch (e: Exception) {
            false
        }
    }
}