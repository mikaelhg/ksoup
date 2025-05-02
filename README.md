# Ksoup: The Ultimate Kotlin DSL for JSoup 🚀

[![Release](https://img.shields.io/badge/Release-v1.0-blue)](https://jitpack.io/#mikaelhg/ksoup)
[![License](https://img.shields.io/badge/License-Apache%202.0-green)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1%2B-purple)](https://kotlinlang.org)

**Ksoup** is an elegant Kotlin DSL wrapper for [JSoup](https://jsoup.org/), 
designed to make web scraping and HTML parsing in Kotlin more intuitive, type-safe, and maintainable. 
Perfect for both simple extractions and complex scraping workflows.

## ✨ Features

- **Type-safe Kotlin DSL** for HTML parsing
- **Seamless JSoup integration** with enhanced Kotlin syntax
- **Reactive-style** data extraction
- **Custom HTTP client** support
- **Clean, maintainable** scraping code
- **Lightweight** with zero runtime dependencies (except JSoup)

## 💡 Usage Examples

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

## 🛠 Roadmap

- [x] Basic HTML extraction
- [x] Custom HTTP client support
- [ ] Multi-page extraction
- [ ] "Next page" iteration
- [ ] Enhanced error handling (4xx/5xx responses)
- [ ] Async support
- [ ] Rate limiting utilities

## 📄 License

Ksoup is released under the [Apache 2.0 License](LICENSE).
