package io.mikael.ksoup

import org.junit.Test

class SimpleTests : StaticWebTest() {

    data class GitHubPage(var username: String = "", var fullName: String = "")

    init {
        contentResolver = {
            when (it) {
                "/mikaelhg" -> "github-mikaelhg.html"
                "/huima" -> "github-huima.html"
                else -> null
            }
        }
    }

    @Test
    fun `simple one page extraction`() {
        val gh = KSoup.extract<GitHubPage> {
            result { GitHubPage() }
            url = server.url("/mikaelhg").toString()
            userAgent = "Mozilla/5.0 UnitTesting/1.0"
            headers["Accept-Encoding"] = "gzip"
            element(".p-nickname") { element, page ->
                page.username = element.text()
            }
            text(".p-name") { text, page ->
                page.fullName = text
            }
        }
        assert(gh.username == "mikaelhg")
        assert(gh.fullName == "Mikael Gueck")
        assert(server.takeRequest().path == "/mikaelhg")
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
        val ex2 = ex1.copy(url = server.url("/huima").toString())
        val gh = ex2.extract()
        assert(gh.username == "huima")
        assert(gh.fullName == "Heimo Laukkanen")
        assert(server.takeRequest().path == "/huima")
    }

}
