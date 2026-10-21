package io.mikael.ksoup.test

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
abstract class StaticWebTest {

    companion object {
        init {
            resourceAsStream("logging.properties").use {
                LogManager.getLogManager().readConfiguration(it)
            }
        }
    }

    private lateinit var server: HttpServer

    protected lateinit var staticContentResolver: (String) -> String?

    /** Request paths which answer with a 302 to the mapped path. */
    protected var redirects: Map<String, String> = emptyMap()

    @Volatile
    private var lastRequest: String? = null

    @Volatile
    protected var lastRequestHeaders: Map<String, String> = emptyMap()

    @BeforeEach
    fun before() {
        server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/") { exchange ->
            exchange.use {
                lastRequest = it.requestURI.path
                lastRequestHeaders = it.requestHeaders.entries.associate { (k, v) -> k.lowercase() to v.first() }
                val filePath = staticContentResolver(it.requestURI.path)
                val redirect = redirects[it.requestURI.path]
                if (redirect != null) {
                    it.responseHeaders.add("Location", redirect)
                    it.sendResponseHeaders(302, -1)
                } else if (filePath == null) {
                    it.sendResponseHeaders(404, -1)
                } else {
                    val bytes = checkNotNull(resource(filePath)) { "Missing test resource: $filePath" }
                        .readText().toByteArray()
                    it.responseHeaders.add("Content-Type", "text/html; charset=utf-8")
                    it.sendResponseHeaders(200, bytes.size.toLong())
                    it.responseBody.write(bytes)
                }
            }
        }
        server.start()
    }

    @AfterEach
    fun after() = server.stop(0)

    protected fun testUrl(path: String) = "http://127.0.0.1:${server.address.port}${path}"

    protected fun assertRequestPath(path: String) = assertEquals(path, lastRequest)

}
