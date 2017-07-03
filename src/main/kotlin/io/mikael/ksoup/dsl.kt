package io.mikael.ksoup

import org.jsoup.Jsoup
import org.jsoup.nodes.Element

object KSoup {

    fun <V: Any> simple(init: SimpleExtractor<V>.() -> Unit) =
            SimpleExtractor<V>().apply(init)

    fun <V: Any> extract(init: SimpleExtractor<V>.() -> Unit) =
            simple(init).extract()

}

interface Extractor<out V> {

    fun extract(): V

}

/**
 * We'll base the simple, detail page and multipage extractors on this.
 */
abstract class ExtractorBase<V: Any> : Extractor<V>, HttpClientBase() {

    internal lateinit var instanceGenerator: () -> V

    internal lateinit var urlGenerator: () -> String

    internal val elementExtractions: MutableList<ElementExtraction<V>> = mutableListOf()

    var url: String
        get() = urlGenerator()
        set(value) { this.urlGenerator = { value } }

    override fun extract(): V {
        val instance = instanceGenerator()
        val doc = Jsoup.connect(urlGenerator()).headers(headers).userAgent(userAgentGenerator()).get()!!
        elementExtractions.forEach { x ->
            doc.select(x.css).forEach { e ->
                x.extract(e, instance)
            }
        }
        return instance
    }

    /**
     * Pass me a generator function for your result type.
     * I'll make you one of these, so you can stuff it with information from the page.
     */
    fun result(generator: () -> V) {
        this.instanceGenerator = generator
    }

    /**
     * Pass me an instance, I'll fill it in from the page.
     */
    fun result(instance: V) = result { instance }

}

internal data class ElementExtraction<in V: Any>(val css: String, val extract: (Element, V) -> Unit)

/**
 * Hit one page, get some data.
 */
class SimpleExtractor<V: Any>(url: String = "") : ExtractorBase<V>() {

    init {
        this.urlGenerator = { url }
    }

    /**
     * If I element a match for your CSS selector, I'll call your extractor function, and pass it an Element.
     */
    fun element(css: String, extract: (Element, V) -> Unit) =
            elementExtractions.add(ElementExtraction(css, extract))

    /**
     * If I element a match for your CSS selector, I'll call your extractor function, and pass it a String.
     */
    fun text(css: String, extract: (String, V) -> Unit) =
            elementExtractions.add(ElementExtraction(css, { e, v -> extract(e.text(), v) }))

}
