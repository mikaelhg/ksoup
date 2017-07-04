package io.mikael.ksoup

import org.jsoup.Jsoup

/**
 * This is absolutely going undergo a total change soon.
 */
open class HttpClient {

    var headers: MutableMap<String, String> = mutableMapOf()

    internal var userAgentGenerator: () -> String = { "Mozilla/5.0 Ksoup/1.0" }

    var userAgent: String
        get() = userAgentGenerator()
        set(value) { userAgentGenerator = { value } }


    fun userAgent(userAgentGenerator: () -> String) {
        this.userAgentGenerator = userAgentGenerator
    }

    protected fun get(url: String) =
            Jsoup.connect(url).headers(headers).userAgent(userAgentGenerator()).get()!!

}
