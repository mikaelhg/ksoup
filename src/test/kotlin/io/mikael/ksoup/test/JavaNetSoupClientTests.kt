package io.mikael.ksoup.test

import io.mikael.ksoup.net.JavaNetSoupClient
import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.net.http.HttpClient

class JavaNetSoupClientTests : StaticWebTest() {

    init {
        staticContentResolver = { if (it == "/page" || it == "/new") "github-mikaelhg.html" else null }
    }

    @Test
    fun `get returns the parsed document`() {
        val doc = JavaNetSoupClient().get(testUrl("/page"), emptyMap(), "UnitTesting/1.0")
        assertEquals("Mikael Gueck", doc.selectFirst(".p-name")?.text())
        assertRequestPath("/page")
        assertEquals("UnitTesting/1.0", lastRequestHeaders["user-agent"])
    }

    @Test
    fun `custom headers are sent and an explicit user agent header wins`() {
        JavaNetSoupClient().get(
            testUrl("/page"), mapOf("X-Test" to "yes", "User-Agent" to "Explicit/1.0"), "Ignored/1.0"
        )
        assertEquals("yes", lastRequestHeaders["x-test"])
        assertEquals("Explicit/1.0", lastRequestHeaders["user-agent"])
    }

    @Test
    fun `custom parser is used`() {
        var parsed = ""
        val client = JavaNetSoupClient(parser = { body ->
            parsed = body
            Jsoup.parse("<p>custom</p>")
        })
        val doc = client.get(testUrl("/page"), emptyMap(), "UnitTesting/1.0")
        assertEquals("custom", doc.selectFirst("p")?.text())
        assertTrue("Mikael Gueck" in parsed)
    }

    @Test
    fun `redirects are followed by default`() {
        redirects = mapOf("/old" to "/new")
        val doc = JavaNetSoupClient().get(testUrl("/old"), emptyMap(), "UnitTesting/1.0")
        assertEquals("Mikael Gueck", doc.selectFirst(".p-name")?.text())
        assertRequestPath("/new")
    }

    @Test
    fun `redirects are not followed when disabled`() {
        redirects = mapOf("/old" to "/new")
        val client = JavaNetSoupClient(redirects = HttpClient.Redirect.NEVER)
        val doc = client.get(testUrl("/old"), emptyMap(), "UnitTesting/1.0")
        assertEquals(null, doc.selectFirst(".p-name"))
        assertRequestPath("/old")
    }

}
