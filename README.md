# Ksoup: Kotlin DSL for JSoup

[![Release](https://img.shields.io/badge/Release-v1.0-blue)](https://jitpack.io/#mikaelhg/ksoup)
[![License](https://img.shields.io/badge/License-Apache%202.0-green)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1%2B-purple)](https://kotlinlang.org)

**Ksoup** is a Kotlin DSL wrapper for [JSoup](https://jsoup.org/), 
designed to make web scraping and HTML parsing in Kotlin more intuitive, type-safe, and maintainable. 

## Features

- **Type-safe Kotlin DSL** for HTML parsing
- **Custom HTTP client** support
- **Clean, maintainable** scraping code
- **Lightweight** with zero runtime dependencies (except JSoup)

### Basic Extraction

```kotlin
import io.mikael.ksoup.KSoup

data class GitHubProfile(var username: String = "", var fullName: String = "")

val profile = KSoup.extract<GitHubProfile> {

    result { GitHubProfile() }
    
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

### Custom HTTP Client

```kotlin
class CustomHttpClient : HttpClient { 
    /* Your implementation here */
}

val profile = KSoup.extract<GitHubProfile> {
    httpClient = CustomHttpClient()
    url = "https://github.com/mikaelhg"
    // Extraction logic...
}
```
