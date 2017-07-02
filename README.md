## Ksoup

A Kotlin DSL for [JSoup](https://jsoup.org/).

The objective is to improve the long-term maintainability of JSoup content extraction units.

```kotlin
data class GitHubPage(var username: String = "", var fullName: String = "")

val gh = KSoup.extract<GitHubPage> {

    result { GitHubPage() }

    url = "https://github.com/mikaelhg"
    
    userAgent = "Mozilla/5.0 Ksoup/1.0"

    headers["Accept-Encoding"] = "gzip"

    text(".p-name") { text, page ->
        page.fullName = text
    }

    element(".p-nickname") { el, page ->
        page.username = el.text()
    }

}
```
