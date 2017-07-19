package io.mikael.ksoup.test

import io.mikael.ksoup.BasicHttpClient
import io.mikael.ksoup.KSoup
import org.jsoup.nodes.Element
import org.junit.Assert
import org.junit.Test

class ClientTests : WebTest() {

    init {
        staticContentResolver = {
            when (it) {
                "/mikaelhg" -> "github-mikaelhg.html"
                "/huima" -> "github-huima.html"
                else -> null
            }
        }
    }

    class BasicSubclass : BasicHttpClient()

    @Test
    fun `create a new basic client`() {
        val gh = KSoup.extract<GitHubPage> {
            result { GitHubPage() }
            url = testUrl("/mikaelhg")
            httpClient = BasicHttpClient()
            element(".p-nickname", Element::text, GitHubPage::username)
            text(".p-name", GitHubPage::fullName)
        }
        Assert.assertEquals("mikaelhg", gh.username)
        Assert.assertEquals("Mikael Gueck", gh.fullName)
        assertRequestPath("/mikaelhg")
    }

    @Test
    fun `subclass the basic client`() {
        val gh = KSoup.extract<GitHubPage> {
            result { GitHubPage() }
            url = testUrl("/mikaelhg")
            httpClient = BasicSubclass()
            element(".p-nickname", Element::text, GitHubPage::username)
            text(".p-name", GitHubPage::fullName)
        }
        Assert.assertEquals("mikaelhg", gh.username)
        Assert.assertEquals("Mikael Gueck", gh.fullName)
        assertRequestPath("/mikaelhg")
    }

}