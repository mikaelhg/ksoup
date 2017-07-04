package io.mikael.ksoup

import okhttp3.mockwebserver.MockResponse
import org.junit.Test
import okhttp3.mockwebserver.MockWebServer



class SimpleTests {

    data class GitHubPage(var username: String = "", var fullName: String = "")

    @Test
    fun `simple one page extraction`() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody(resource("github-mikaelhg.html")))
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
        }
    }

    @Test
    fun `copy extractors`() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody(resource("github-huima.html")))
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
        }
    }

    internal fun resource(filename: String) = javaClass.classLoader.getResource(filename).readText()

}
