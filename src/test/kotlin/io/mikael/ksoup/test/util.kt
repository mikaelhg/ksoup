package io.mikael.ksoup.test

import io.mikael.ksoup.getLogger
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import java.util.logging.LogManager

internal val resource = object {}::class.java.classLoader::getResource

internal val resourceAsStream = object {}::class.java.classLoader::getResourceAsStream

/**
 * Handle MockHttpServer's requests by looking up and serving a static file from classpath.
 */
internal class StaticDispatcher(private val resolver: (String) -> String?): Dispatcher() {

    private fun r(n: Int): MockResponse = MockResponse().setResponseCode(n)

    override fun dispatch(request: RecordedRequest?) =
            resolver(request!!.path)
                    ?.let { resource(it) }
                    ?.let { r(200).setBody(it.readText()) }
                    ?: r(404)

}

/**
 * A base for all integration tests which need to connect to a mock HTTP server.
 */
open class WebTest {

    protected lateinit var server: MockWebServer

    protected lateinit var staticContentResolver: (String) -> String?

    protected val log = getLogger(this.javaClass)

    init {
        resourceAsStream("logging.properties").use {
            LogManager.getLogManager().readConfiguration(it)
        }
    }

    @Before
    fun before() {
        server = MockWebServer().apply {
            this.setDispatcher(StaticDispatcher(staticContentResolver))
            this.start()
        }
    }

    @After
    fun after(): Unit = server.close()

    protected fun testUrl(path: String) = server.url(path)!!.toString()

    protected fun assertRequestPath(path: String) = assertEquals(path, server.takeRequest().path)

}
