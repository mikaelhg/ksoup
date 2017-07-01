package io.mikael.ksoup

import org.junit.Test

data class GitHubPage(var username: String = "", var fullName: String = "")

class LanguageTests {


    @Test
    fun `simple one page extraction`() {

        val gh = KSoup.extract<GitHubPage> {

            result { GitHubPage() }

            url = "https://github.com/mikaelhg"

            userAgent = "Mozilla/5.0 UnitTesting/1.0"

            find(".p-nickname") { element, page ->
                page.username = element.text()
            }

            findText(".p-name") { text, page ->
                page.fullName = text
            }
        }

        assert(gh.username == "mikaelhg")
        assert(gh.fullName == "Mikael Gueck")
    }

    @Test
    fun `simple multipage extraction`() {

    }

}
