package io.mikael.ksoup.net

import org.jsoup.nodes.Document

/**
 * If you want to use the library with Apache HttpClient, okhttp3, or some other
 * HTTP client implementation, implement a HttpClient and a ResponseToDocumentParser.
 */
fun interface SoupClient {
    fun get(url: String, headers: Map<String, String>, userAgent: String): Document
}
