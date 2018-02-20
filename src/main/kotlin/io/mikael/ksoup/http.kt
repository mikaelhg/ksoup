package io.mikael.ksoup

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.HttpURLConnection

/**
 * The essential basis for all extractors which require access to the web.
 */
open class WebSupport {

    var headers: MutableMap<String, String> = mutableMapOf()

    var httpClient : HttpClient = JdkHttpClient()

    internal var userAgentGenerator: () -> String = { "Mozilla/5.0 Ksoup/1.0" }

    var userAgent: String
        get() = userAgentGenerator()
        set(value) { userAgentGenerator = { value } }

    fun userAgent(userAgentGenerator: () -> String) {
        this.userAgentGenerator = userAgentGenerator
    }

    protected fun get(url: String) : Document
            = httpClient.get(url, headers, userAgent)

}

/**
 * If you want to use the library with Apache HttpClient, okhttp3, or some other
 * HTTP client implementation, implement a HttpClient and a ResponseToDocumentParser.
 */
interface HttpClient {
    fun get(url: String, headers: Map<String, String>, userAgent: String) : Document
}

/**
 * The most basic, java.net.URL-using HTTP client, with no additional dependencies.
 */
open class JdkHttpClient(
        private val parser: ResponseToDocumentParser<String, Document> = StringToDocumentParser(),
        private val connectTimeout: Int = 1000,
        private val readTimeout: Int = 1000
) : HttpClient {
    override fun get(url: String, headers: Map<String, String>, userAgent: String) : Document {
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

/**
 * Accept the HttpClient result class as input, produce a parsed document,
 * such as the JSoup Document.
 */
interface ResponseToDocumentParser<in S, out D> {
    fun parse(data: S) : D
}

open class StringToDocumentParser : ResponseToDocumentParser<String, Document> {
    override fun parse(data: String) = Jsoup.parse(data)!!
}
