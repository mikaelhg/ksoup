package io.mikael.ksoup.net

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.HttpURLConnection

/**
 * The most basic, java.net.URL-using HTTP client, with no additional dependencies.
 */
open class UrlConnectionSoupClient(
    private val parser: (String) -> Document = { Jsoup.parse(it) },
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
            return parser(it.reader().readText())
        }
    }
}