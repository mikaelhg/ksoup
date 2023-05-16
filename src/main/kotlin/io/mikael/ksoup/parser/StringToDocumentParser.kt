package io.mikael.ksoup.parser

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

open class StringToDocumentParser : ResponseToDocumentParser<String, Document> {
    override fun parse(data: String) = Jsoup.parse(data)
}
