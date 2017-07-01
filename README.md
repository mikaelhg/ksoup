## Ksoup

A Kotlin DSL for JSoup.

```kotlin
data class GitHubPage(var username: String = "", var fullName: String = "")

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
```
