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

data class ElementExtraction<in V: Any>(val css: String, val extract: (Element, V) -> Unit)

class SimpleExtractor<V: Any>(var url: String = "") : Extractor<V> {

    private lateinit var instanceGenerator: () -> V

    private val elementExtractions: MutableList<ElementExtraction<V>> = mutableListOf()

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

    /**
     * If I find a match for your CSS selector, I'll call your extractor function, and pass it an Element.
     */
    fun find(css: String, extract: (Element, V) -> Unit) =
            elementExtractions.add(ElementExtraction(css, extract))

    /**
     * If I find a match for your CSS selector, I'll call your extractor function, and pass it a String.
     */
    fun findText(css: String, extract: (String, V) -> Unit) =
            elementExtractions.add(ElementExtraction(css, { e, v -> extract(e.text(), v) }))

    override fun extract(): V {
        val instance = instanceGenerator()
        val doc = Jsoup.connect(url)?.get()!!
        elementExtractions.forEach {
            val e = doc.select(it.css)?.first()
            if (null != e) {
                it.extract(e, instance)
            }
        }
        return instance
    }

}
