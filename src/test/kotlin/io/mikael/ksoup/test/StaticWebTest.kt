package io.mikael.ksoup.test

import io.undertow.Undertow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import java.util.logging.LogManager

internal val resource = object {}::class.java.classLoader::getResource

internal val resourceAsStream = object {}::class.java.classLoader::getResourceAsStream

/**
 * A base for all integration tests which need to connect to a mock HTTP server.
 */
abstract class StaticWebTest {

    companion object {
        private const val PORT = 45762
    }

    private lateinit var server: Undertow

    protected lateinit var staticContentResolver: (String) -> String?

    private var lastRequest: String? = null

    init {
        resourceAsStream("logging.properties").use {
            LogManager.getLogManager().readConfiguration(it)
        }
    }

    @BeforeEach
    fun before() {
        server = Undertow.builder()
            .addHttpListener(PORT, "127.0.0.1")
            .setHandler { exchange ->
                lastRequest = exchange.requestURI
                when (val filePath = staticContentResolver(exchange.requestURI)) {
                    null -> {
                        exchange.setStatusCode(404)
                    }

                    else -> {
                        exchange.setStatusCode(200)
                        exchange.responseSender.send(resource(filePath)!!.readText())
                    }
                }
            }
            .build()
        server.start()
    }

    @AfterEach
    fun after() = server.stop()

    protected fun testUrl(path: String) = "http://127.0.0.1:${PORT}${path}"

    protected fun assertRequestPath(path: String) = assertEquals(path, lastRequest)

}
