package io.mikael.ksoup.extractor

import org.jsoup.nodes.Element
import kotlin.reflect.KMutableProperty1

/**
 * Hit one page, get some data.
 */
class SimpleExtractor<V: Any>(url: String = "") : Extractor<V>() {

    init {
        this.urlGenerator = { url }
    }

    private var extractionCommands: MutableList<ExtractionCommand<V>> = mutableListOf()

    override fun extract(): V {
        val instance = instanceGenerator()
        val doc = document()
        extractionCommands.forEach { it.extract(doc, instance) }
        return instance
    }

    /**
     * If I find a match for your CSS selector, I'll call your extractor function, and pass it an Element.
     *
     * ## Usage:
     * ```kotlin
     * element(".p-nickname") { element, page ->
     *     page.username = element.text()
     * }
     * ```
     */
    fun element(css: String, extract: (Element, V) -> Unit) {
        extractionCommands.add(ExtractionCommand(css, extract))
    }

    /**
     * If I find a match for your CSS selector, I'll call your extractor function, and pass it an Element.
     *
     * ## Usage:
     * ```kotlin
     * element(".p-nickname", Element::text, GitHubPage::username)
     * ```
     */
    fun <P> element(css: String, from: Element.() -> P, toProperty: KMutableProperty1<in V, P>) {
        extractionCommands.add(ExtractionCommand(css) { e, v -> toProperty.set(v, e.from()) })
    }

    /**
     * If I find a match for your CSS selector, I'll call your extractor function, and pass it a String.
     *
     * ## Usage:
     * ```kotlin
     * text(".p-name") { text, page ->
     *     page.fullName = text
     * }
     * ```
     */
    fun text(css: String, extract: (String, V) -> Unit) {
        extractionCommands.add(ExtractionCommand(css) { e, v -> extract(e.text(), v) })
    }

    /**
     * If I find a match for your CSS selector, I'll stuff the results into your instance property.
     *
     * ## Usage:
     * ```kotlin
     * text(".p-name", GitHubPage::fullName)
     * ```
     */
    fun text(css: String, property: KMutableProperty1<V, String>) {
        extractionCommands.add(ExtractionCommand(css) { e, v -> property.set(v, e.text()) })
    }

    /**
     * Call for a copy. Pass in the values you want to change in the new instance.
     */
    fun copy(urlGenerator: () -> String = this.urlGenerator,
             instanceGenerator: () -> V = this.instanceGenerator,
             extractionCommands: MutableList<ExtractionCommand<V>> = this.extractionCommands,
             userAgent: String? = null,
             userAgentGenerator: () -> String = this.userAgentGenerator,
             url: String? = null,
             instance: V? = null) =
            SimpleExtractor<V>().apply {
                this@apply.urlGenerator = if (url == null) urlGenerator else ({ url })
                this@apply.instanceGenerator = if (instance == null) instanceGenerator else ({ instance })
                this@apply.userAgentGenerator = if (userAgent == null) userAgentGenerator else ({ userAgent })
                this@apply.extractionCommands = extractionCommands
            }

}
