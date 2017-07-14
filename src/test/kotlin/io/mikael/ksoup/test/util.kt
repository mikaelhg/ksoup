package io.mikael.ksoup.test

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import java.util.logging.LogManager

data class GitHubPage(var username: String = "", var fullName: String = "")

internal val resource = StaticWebTest::class.java.classLoader::getResource

internal val resourceAsStream = StaticWebTest::class.java.classLoader::getResourceAsStream

internal class StaticDispatcher(private val resolver: (String) -> String?): Dispatcher() {

    override fun dispatch(request: RecordedRequest?): MockResponse {
        val resourcePath = resolver(request!!.path)
        val resource = when (resourcePath) {
            null -> null
            else -> resource(resourcePath)
        }
        return when (resource) {
            null -> MockResponse().setResponseCode(404)
            else -> MockResponse().setResponseCode(200).setBody(resource.readText())
        }
    }

}

open class StaticWebTest {

    protected lateinit var server: MockWebServer

    protected lateinit var staticContentResolver: (String) -> String?

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
