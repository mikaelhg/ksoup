## Ksoup

A Kotlin DSL for [JSoup](https://jsoup.org/).

The objective is to improve the long-term maintainability of JSoup content extraction units.

Current status on 2017-07-21: totally useable for simple extractions, 
but multi-page extractions and "next page" iteration aren't implemented yet.

Next: error handling, 4xx, 5xx and other responses and exceptions.

Available from JitPack.

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
