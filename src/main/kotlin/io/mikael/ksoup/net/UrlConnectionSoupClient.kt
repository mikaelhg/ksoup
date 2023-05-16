package io.mikael.ksoup.net

import io.mikael.ksoup.parser.ResponseToDocumentParser
import io.mikael.ksoup.parser.StringToDocumentParser
import org.jsoup.nodes.Document
import java.net.HttpURLConnection

/**
 * The most basic, java.net.URL-using HTTP client, with no additional dependencies.
 */
open class UrlConnectionSoupClient(
    private val parser: ResponseToDocumentParser<String, Document> = StringToDocumentParser(),
    private val connectTimeout: Int = 1000,
    private val readTimeout: Int = 1000
): SoupClient {
    override fun get(url: String, headers: Map<String, String>, userAgent: String): Document {
        val con = java.net.URL(url).openConnection() as HttpURLConnection
        headers.forEach(con::setRequestProperty)
        con.setRequestProperty("User-Agent", userAgent)
        con.connectTimeout = connectTimeout
        con.readTimeout = readTimeout
        con.requestMethod = "GET"
        con.useCaches = false
        con.doInput = true
        con.doOutput = false
        con.inputStream.use {
            return parser.parse(it.reader().readText())
        }
    }
}