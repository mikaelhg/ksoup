package io.mikael.ksoup.net

import org.jsoup.nodes.Document

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
