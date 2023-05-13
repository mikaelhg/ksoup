package io.mikael.ksoup

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.Authenticator
import java.net.HttpURLConnection
import java.net.ProxySelector
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpClient.Builder.NO_PROXY
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.time.Duration

/**
 * The essential basis for all extractors which require access to the web.
 */
open class WebSupport {

    var headers: MutableMap<String, String> = mutableMapOf()

    var soupClient: SoupClient = JavaNetSoupClient()

    internal var userAgentGenerator: () -> String = { "Mozilla/5.0 Ksoup/1.0" }

    var userAgent: String
        get() = userAgentGenerator()
        set(value) { userAgentGenerator = { value } }

    fun userAgent(userAgentGenerator: () -> String) {
        this.userAgentGenerator = userAgentGenerator
    }

    protected fun get(url: String): Document = soupClient.get(url, headers, userAgent)

}

/**
 * If you want to use the library with Apache HttpClient, okhttp3, or some other
 * HTTP client implementation, implement a HttpClient and a ResponseToDocumentParser.
 */
interface SoupClient {
    fun get(url: String, headers: Map<String, String>, userAgent: String): Document
}

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

/**
 * Java 11 introduced a much better stock HTTP client.
 */
open class JavaNetSoupClient(
    private val parser: ResponseToDocumentParser<String, Document> = StringToDocumentParser(),
    private val connectTimeout: Duration = Duration.ofSeconds(1),
    private val readTimeout: Duration = Duration.ofSeconds(1),
    private val proxySelector: ProxySelector = NO_PROXY,
    private val authenticator: Authenticator? = Authenticator.getDefault(),
    private val redirects: HttpClient.Redirect = HttpClient.Redirect.NORMAL,
    private val httpVersion: HttpClient.Version = HttpClient.Version.HTTP_1_1
): SoupClient {
    override fun get(url: String, headers: Map<String, String>, userAgent: String): Document {
        val client = HttpClient.newBuilder()
            .apply {
                connectTimeout.let(this::connectTimeout)
                httpVersion.let(this::version)
                redirects.let(this::followRedirects)
                authenticator?.let(this::authenticator)
                proxySelector.let(this::proxy)
            }
            .build()
        val request = HttpRequest.newBuilder().GET().uri(URI.create(url))
            .apply {
                readTimeout.let(this::timeout)
                headers.forEach(this::header)
            }
            .build()
        val response = client.send(request, BodyHandlers.ofString())
        return parser.parse(response.body())
    }
}

/**
 * Accept the HttpClient result class as input, produce a parsed document,
 * such as the JSoup Document.
 */
interface ResponseToDocumentParser<in S, out D> {
    fun parse(data: S): D
}

open class StringToDocumentParser : ResponseToDocumentParser<String, Document> {
    override fun parse(data: String) = Jsoup.parse(data)
}
