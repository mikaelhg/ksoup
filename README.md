## Ksoup

A Kotlin DSL for JSoup.

The objective is to improve the long-term maintainability of JSoup content extraction units.

```kotlin
data class GitHubPage(var username: String = "", var fullName: String = "")

val gh = KSoup.extract<GitHubPage> {

    url = "https://github.com/mikaelhg"

    result { GitHubPage() }

    find(".p-nickname") { element, page ->
        page.username = element.text()
    }

    findText(".p-name") { text, page ->
        page.fullName = text
    }

}
```
