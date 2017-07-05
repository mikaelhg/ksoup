package io.mikael.ksoup.test

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before

internal class StaticDispatcher(private val resolver: (String) -> String?): Dispatcher() {

    private fun readText(filename: String) = javaClass.classLoader.getResource(filename)!!.readText()

    override fun dispatch(request: RecordedRequest?): MockResponse {
        val resourcePath = resolver(request!!.path)
        return when (resourcePath) {
            null -> MockResponse().setResponseCode(404)
            else -> MockResponse().setResponseCode(200).setBody(readText(resourcePath))
        }
    }

}

open class StaticWebTest {

    protected lateinit var server: MockWebServer

    protected lateinit var staticContentResolver: (String) -> String?

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
