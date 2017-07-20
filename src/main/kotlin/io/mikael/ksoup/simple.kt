package io.mikael.ksoup

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import kotlin.reflect.KMutableProperty1

/**
 * A container for each command to extract information from an Element,
 * and stuff it into an object instance field.
 */
data class ExtractionCommand<in V: Any>(val css: String, val extract: (Element, V) -> Unit) {

    fun extract(doc: Document, item: V) = doc.select(css).forEach { element -> extract(element, item) }

}

@KSoupDsl
abstract class ExtractorBase<V : Any> : Extractor<V>, HttpSupport() {

    internal lateinit var instanceGenerator: () -> V

    internal lateinit var urlGenerator: () -> String

    var url: String
        get() = urlGenerator()
        set(value) { this.urlGenerator = { value } }

    protected fun document() = get(url)

    /**
     * Pass me a generator function for your result type.
     * I'll make you one of these, so you can stuff it with information from the page.
     */
    fun result(generator: () -> V) {
        this.instanceGenerator = generator
    }

    /**
     * Pass me an instance, and I'll fill it in from the page.
     */
    fun result(instance: V) = result { instance }

}

/**
 * Hit one page, get some data.
 */
open class SimpleExtractor<V: Any>(url: String = "") : ExtractorBase<V>() {

    init {
        this.urlGenerator = { url }
    }

    protected var extractionCommands: MutableList<ExtractionCommand<V>> = mutableListOf()

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
        extractionCommands.add(ExtractionCommand(css, { e, v -> toProperty.set(v, e.from()) }))
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
        extractionCommands.add(ExtractionCommand(css, { e, v -> extract(e.text(), v) }))
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
        extractionCommands.add(ExtractionCommand(css, { e, v -> property.set(v, e.text()) }))
    }

    /**
     * Call for a copy. Pass in the values you want to change in the new instance.
     */
    fun copy(urlGenerator: () -> String = this.urlGenerator,
             instanceGenerator: () -> V = this.instanceGenerator,
             extractionCommands: MutableList<ExtractionCommand<V>> = this.extractionCommands,
             url: String? = null,
             instance: V? = null) =
            SimpleExtractor<V>().apply {
                this.urlGenerator = if (url == null) urlGenerator else ({ url })
                this.instanceGenerator = if (instance == null) instanceGenerator else ({ instance })
                this.extractionCommands = extractionCommands
            }

}
