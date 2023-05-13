package io.mikael.ksoup.test

import io.mikael.ksoup.KSoup
import org.jsoup.nodes.Element
import org.junit.jupiter.api.Assertions.*
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
            result { GitHubPage() }
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
    }

}
