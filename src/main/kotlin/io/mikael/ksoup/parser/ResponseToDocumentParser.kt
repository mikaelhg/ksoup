package io.mikael.ksoup.parser

/**
 * Accept the HttpClient result class as input, produce a parsed document,
 * such as the JSoup Document.
 */
interface ResponseToDocumentParser<in S, out D> {
    fun parse(data: S): D
}
