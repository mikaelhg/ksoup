package io.mikael.ksoup.test

import io.mikael.ksoup.KSoup
import org.junit.Assert.assertEquals
import org.junit.Test

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
            text(".p-name", GitHubPage::fullName)
        }
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
            url = "https://github.com/mikaelhg"
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
    }

}
