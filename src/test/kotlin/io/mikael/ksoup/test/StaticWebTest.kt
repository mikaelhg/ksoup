package io.mikael.ksoup.test

import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import java.net.InetSocketAddress
import java.util.logging.LogManager

internal val resource = object {}::class.java.classLoader::getResource

internal val resourceAsStream = object {}::class.java.classLoader::getResourceAsStream

/**
 * A base for all integration tests which need to connect to a mock HTTP server.
 */
open class StaticWebTest {

    companion object {
        private const val PORT = 45762
    }

    protected lateinit var server: HttpServer

    protected lateinit var staticContentResolver: (String) -> String?

    private var lastRequest: String? = null

    init {
        resourceAsStream("logging.properties").use {
            LogManager.getLogManager().readConfiguration(it)
        }
    }

    @BeforeEach
    fun before() {
        server = HttpServer.create(InetSocketAddress(PORT), 1).apply {
            createContext("/").handler = HttpHandler { exchange ->
                lastRequest = exchange.requestURI.path
                when (val filePath = staticContentResolver(exchange.requestURI.path)) {
                    null -> {
                        exchange.sendResponseHeaders(404, 0)
                    }
                    else -> {
                        val bytes = resource(filePath)!!.readBytes()
                        exchange.sendResponseHeaders(200, bytes.size.toLong())
                        exchange.responseBody.write(bytes)
                    }
                }
                exchange.close()
            }
            this.start()
        }
    }

    @AfterEach
    fun after() = server.stop(0)

    protected fun testUrl(path: String) = "http://127.0.0.1:${PORT}${path}"

    protected fun assertRequestPath(path: String) = assertEquals(path, lastRequest)

}
