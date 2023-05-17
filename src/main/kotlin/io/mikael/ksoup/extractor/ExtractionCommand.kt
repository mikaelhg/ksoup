package io.mikael.ksoup.extractor

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * A container for each command to extract information from an Element,
 * and stuff it into an object instance field.
 */
data class ExtractionCommand<in V : Any>(private val css: String, private val command: (Element, V) -> Unit) {

    fun extract(doc: Document, item: V) = doc.select(css).forEach { element -> command(element, item) }

}
