## Ksoup

A Kotlin DSL for [JSoup](https://jsoup.org/).
For the long-term maintainability of JSoup content extraction units.

[![](https://jitpack.io/v/mikaelhg/ksoup.svg)](https://jitpack.io/#mikaelhg/ksoup)

Current status: totally useable for simple extractions, 
but multi-page extractions and "next page" iteration aren't implemented yet.

Next: error handling, 4xx, 5xx and other responses and exceptions.

```kotlin
import io.mikael.ksoup.KSoup

data class GitHubPage(var username: String = "", var fullName: String = "")

val gh : GitHubPage = KSoup.extract<GitHubPage> {

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

class ApacheHttpClient : HttpClient { /* ... implement a method ... */ }

val gh = KSoup.extract<GitHubPage> {

    httpClient = ApacheHttpClient()

    /* ... */
}
```
