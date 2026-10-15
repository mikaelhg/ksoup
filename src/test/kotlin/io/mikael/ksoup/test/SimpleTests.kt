package io.mikael.ksoup.test

import io.mikael.ksoup.KSoup
import io.mikael.ksoup.extractor.SimpleExtractor
import io.mikael.ksoup.net.SoupClient
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SimpleTests : StaticWebTest() {

    init {
        staticContentResolver = {
            when (it) {
                "/mikaelhg" -> "github-mikaelhg.html"
                "/huima" -> "github-huima.html"
                else -> null
            }
        }
    }

    @Test
    fun `property reference`() {
        val gh = KSoup.extract<GitHubPage> {
            result { GitHubPage() }
            url = testUrl("/mikaelhg")
            element(".p-nickname", Element::text, GitHubPage::username)
            text(".p-name", GitHubPage::fullName)
        }
        assertEquals("mikaelhg", gh.username)
        assertEquals("Mikael Gueck", gh.fullName)
        assertRequestPath("/mikaelhg")
    }

    @Test
    fun `simple one page extraction`() {
        val gh = KSoup.extract<GitHubPage> {
            result(GitHubPage())
            url = testUrl("/mikaelhg")
            userAgent = "Mozilla/5.0 UnitTesting/1.0"
            headers["Accept-Encoding"] = "gzip"
            element(".p-nickname") { element, page ->
                page.username = element.text()
            }
            text(".p-name") { text, page ->
                page.fullName = text
            }
        }
        assertEquals("mikaelhg", gh.username)
        assertEquals("Mikael Gueck", gh.fullName)
        assertRequestPath("/mikaelhg")
    }

    @Test
    fun `user agent is sent`() {
        KSoup.extract<GitHubPage> {
            result { GitHubPage() }
            url = testUrl("/mikaelhg")
            userAgent = "Mozilla/5.0 UnitTesting/1.0"
        }
        assertEquals("Mozilla/5.0 UnitTesting/1.0", lastRequestHeaders["user-agent"])
    }

    @Test
    fun `explicit user agent header wins`() {
        KSoup.extract<GitHubPage> {
            result { GitHubPage() }
            url = testUrl("/mikaelhg")
            userAgent = "Ignored/1.0"
            headers["User-Agent"] = "Explicit/1.0"
        }
        assertEquals("Explicit/1.0", lastRequestHeaders["user-agent"])
    }

    @Test
    fun `copy extractors`() {
        val ex1 = KSoup.simple<GitHubPage> {
            result { GitHubPage() }
            url = testUrl("/mikaelhg")
            element(".p-nickname") { element, page ->
                page.username = element.text()
            }
            text(".p-name") { text, page ->
                page.fullName = text
            }
        }
        val ex2 = ex1.copy(url = testUrl("/huima"))
        val gh = ex2.extract()
        assertEquals("huima", gh.username)
        assertEquals("Heimo Laukkanen", gh.fullName)
        assertRequestPath("/huima")
        assertNotEquals(ex1.url, ex2.url)
    }

    @Test
    fun `copy keeps client and headers`() {
        var clientCalled = false
        val ex1 = KSoup.simple<GitHubPage> {
            result { GitHubPage() }
            url = testUrl("/mikaelhg")
            headers["X-Test"] = "yes"
            soupClient = SoupClient { u, h, ua ->
                clientCalled = true
                assertEquals("yes", h["X-Test"])
                Jsoup.parse("<html></html>")
            }
        }
        ex1.copy(url = testUrl("/huima")).extract()
        assertTrue(clientCalled)
    }

    @Test
    fun `properties change in copy`() {
        val ex1 = KSoup.simple<GitHubPage> {
            result { GitHubPage() }
            url = testUrl("/mikaelhg")
            element(".p-nickname") { element, page ->
                page.username = element.text()
            }
            text(".p-name") { text, page ->
                page.fullName = text
            }
        }
        val ex2 = ex1.copy(url = testUrl("/huima"))
        assertNotEquals(ex1.url, ex2.url)
        val ex3 = ex1.copy(urlGenerator = { testUrl("/huima") })
        assertNotEquals(ex1.url, ex3.url)
    }

    @Test
    fun `direct constructor call for SimpleExtractor`() {
        val ex1 = SimpleExtractor<GitHubPage>(testUrl("/huima")).apply {
            result { GitHubPage() }
            url = testUrl("/mikaelhg")
            element(".p-nickname") { element, page ->
                page.username = element.text()
            }
            text(".p-name") { text, page ->
                page.fullName = text
            }
        }
        val gh = ex1.extract()
        assertEquals("mikaelhg", gh.username)
    }

}
