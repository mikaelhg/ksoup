package io.mikael.ksoup

import org.junit.Test

data class GitHubPage(var username: String = "", var fullName: String = "")

class LanguageTests {


    @Test
    fun `simple one page extraction`() {

        val gh = KSoup.extract<GitHubPage> {

            url = "https://github.com/mikaelhg"

            input { GitHubPage() }

            find(".p-nickname") { element, page ->
                page.username = element.text()
            }

            findText(".p-name") { text, page ->
                page.fullName = text
            }

        }

        println(gh)
    }

}
