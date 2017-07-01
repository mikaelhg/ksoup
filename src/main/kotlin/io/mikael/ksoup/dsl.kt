package io.mikael.ksoup

import org.jsoup.Jsoup
import org.jsoup.nodes.Element

object KSoup {

    fun <V: Any> simple(init: SimpleExtractor<V>.() -> Unit) = SimpleExtractor<V>().apply(init)

    fun <V: Any> extract(init: SimpleExtractor<V>.() -> Unit) = simple(init).extract()

}

interface Extractor<out V> {

    fun extract(): V

}

data class ElementExtraction<in V: Any>(val css: String, val extract: (Element, V) -> Unit)

class SimpleExtractor<V: Any>(var url: String = "") : Extractor<V> {

    private lateinit var instanceGenerator: () -> V

    private val elementExtractions: MutableList<ElementExtraction<V>> = mutableListOf()

    fun input(generator: () -> V) {
        this.instanceGenerator = generator
    }

    fun input(instance: V) = input { instance }

    fun find(css: String, extract: (Element, V) -> Unit) =
            elementExtractions.add(ElementExtraction(css, extract))

    fun findText(css: String, extract: (String, V) -> Unit) =
            elementExtractions.add(ElementExtraction(css, { e, v -> extract(e.text(), v) }))

    override fun extract(): V {
        val instance = instanceGenerator()
        val doc = Jsoup.connect(url)?.get()!!
        elementExtractions.forEach {
            it.extract(doc.select(it.css)?.first()!!, instance)
        }
        return instance
    }

}
