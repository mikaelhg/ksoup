package io.mikael.ksoup.test

import io.mikael.ksoup.KSoup
import io.mikael.ksoup.net.SoupClient
import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

class MockedClientTests {

    @Test
    fun `mocked client`() {
        val gh = KSoup.extract<GitHubPage> {
            result { GitHubPage() }
            soupClient = fakeClient("mikaelhg")
            url = "https://github.com/mikaelhg"
            text(".p-name") { text, page ->
                page.fullName = text
            }
        }
        Assertions.assertEquals("Mikael Gueck", gh.fullName)
    }

    private fun fakeClient(username: String) = SoupClient { _, _, _ ->
        Jsoup.parse(File("./src/test/resources/github-$username.html"))
    }

}
