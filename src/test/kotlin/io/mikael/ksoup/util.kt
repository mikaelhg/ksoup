package io.mikael.ksoup

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before

internal class StaticDispatcher(private val resolver: (String) -> String?): Dispatcher() {

    private fun readText(filename: String) = javaClass.classLoader.getResource(filename).readText()

    private fun r200() = MockResponse().setResponseCode(200)
    private fun r404() = MockResponse().setResponseCode(404)

    override fun dispatch(request: RecordedRequest?): MockResponse {
        val resourcePath = resolver(request!!.path)
        return when (resourcePath) {
            null -> r404()
            else -> r200().setBody(readText(resourcePath))
        }
    }

}

open class StaticWebTest {

    protected lateinit var server: MockWebServer

    protected lateinit var contentResolver: (String) -> String?

    @Before
    fun before() {
        server = MockWebServer().apply {
            this.setDispatcher(StaticDispatcher(contentResolver))
            this.start()
        }
    }

    @After
    fun after(): Unit = server.close()

}
