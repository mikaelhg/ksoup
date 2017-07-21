package io.mikael.ksoup

@DslMarker
annotation class KSoupDsl

/**
 * Invoke the methods on this main DSL class to fetch data using JSoup.
 */
object KSoup {

    /**
     * Get a web page and extract some content from it.
     *
     * ## Usage:
     *
     * If you want to extract content from GitHub into an instance of the data class GitHubPage:
     *
     * ```kotlin
     * val gh : GitHubPage = KSoup.extract<GitHubPage> {
     *
     *     url = "https://github.com/mikaelhg"
     *
     *     result { GitHubPage() }               // instantiate (or reuse) your result object
     *
     *     userAgent = "Mozilla/5.0 Ksoup/1.0"
     *
     *     headers["Accept-Encoding"] = "gzip"
     *
     *     text(".p-name") { text, page ->       // find all elements for the selector
     *         page.fullName = text              //     then run this code for each
     *     }
     *
     *     text(".p-name", GitHubPage::fullName)
     *
     *     element(".p-nickname") { el, page ->  // find all elements for the selector
     *         page.username = el.text()         //     then run this code for each
     *     }
     *
     *     element(".p-nickname", Element::text, GitHubPage::username)
     * }
     * ```
     */
    fun <V : Any> extract(init: SimpleExtractor<V>.() -> Unit): V
            = SimpleExtractor<V>().apply(init).extract()

    /**
     *
     */
    fun <V : Any> simple(init: SimpleExtractor<V>.() -> Unit): SimpleExtractor<V>
            = SimpleExtractor<V>().apply(init)

}

/**
 * Don't know yet if we'll be keeping this, or just using the ExtractorBase.
 * Depends on how the more complicated use cases pan out.
 */
interface Extractor<out V> {
    fun extract(): V
}
