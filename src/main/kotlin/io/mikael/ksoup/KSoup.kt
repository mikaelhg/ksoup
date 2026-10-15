package io.mikael.ksoup

import io.mikael.ksoup.extractor.SimpleExtractor

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
     *     headers["Accept-Language"] = "en"
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
     * Configure an extractor without running it, so you can run it later, or more than once.
     *
     * ## Usage:
     *
     * If you want to build an extractor once, then point copies of it at different pages:
     *
     * ```kotlin
     * val profile = KSoup.simple<GitHubPage> {
     *     result { GitHubPage() }               // a new result object for each extraction
     *     url = "https://github.com/mikaelhg"
     *     text(".p-name", GitHubPage::fullName)
     * }
     *
     * val mikael = profile.extract()
     * val heimo = profile.copy(url = "https://github.com/huima").extract()
     * ```
     */
    fun <V : Any> simple(init: SimpleExtractor<V>.() -> Unit): SimpleExtractor<V>
            = SimpleExtractor<V>().apply(init)

}
