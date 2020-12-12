package io.mikael.ksoup.test

import io.mikael.ksoup.UrlConnectionSoupClient
import io.mikael.ksoup.KSoup
import org.jsoup.nodes.Element
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ClientTests : StaticWebTest() {

    init {
        staticContentResolver = {
            when (it) {
                "/mikaelhg" -> "github-mikaelhg.html"
                "/huima" -> "github-huima.html"
                else -> null
            }
        }
    }

    class UrlConnectionSubclass : UrlConnectionSoupClient()

    @Test
    fun `create a new basic client`() {
        val gh = KSoup.extract<GitHubPage> {
            result { GitHubPage() }
            url = testUrl("/mikaelhg")
            soupClient = UrlConnectionSoupClient()
            element(".p-nickname", Element::text, GitHubPage::username)
            text(".p-name", GitHubPage::fullName)
        }
        assertEquals("mikaelhg", gh.username)
        assertEquals("Mikael Gueck", gh.fullName)
        assertRequestPath("/mikaelhg")
    }

    @Test
    fun `subclass the basic client`() {
        val gh = KSoup.extract<GitHubPage> {
            result { GitHubPage() }
            url = testUrl("/mikaelhg")
            soupClient = UrlConnectionSubclass()
            element(".p-nickname", Element::text, GitHubPage::username)
            text(".p-name", GitHubPage::fullName)
        }
        assertEquals("mikaelhg", gh.username)
        assertEquals("Mikael Gueck", gh.fullName)
        assertRequestPath("/mikaelhg")
    }

}
