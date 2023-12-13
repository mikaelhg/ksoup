package io.mikael.ksoup.net

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.Authenticator
import java.net.ProxySelector
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

/**
 * Java 11 introduced a much better stock HTTP client.
 */
open class JavaNetSoupClient(
    private val parser: (String) -> Document = { Jsoup.parse(it) },
    private val connectTimeout: Duration = Duration.ofSeconds(1),
    private val readTimeout: Duration = Duration.ofSeconds(1),
    private val proxySelector: ProxySelector = HttpClient.Builder.NO_PROXY,
    private val authenticator: Authenticator? = Authenticator.getDefault(),
    private val redirects: HttpClient.Redirect = HttpClient.Redirect.NORMAL,
    private val httpVersion: HttpClient.Version = HttpClient.Version.HTTP_2
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
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return parser(response.body())
    }
}
