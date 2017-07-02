package io.mikael.ksoup


open class HttpClientBase {

    val headers: MutableMap<String, String> = mutableMapOf()

    internal var userAgentGenerator: () -> String = { "Mozilla/5.0 Ksoup/1.0" }

    var userAgent: String
        get() = userAgentGenerator()
        set(value) { this.userAgentGenerator = { value } }


    fun userAgent(userAgentGenerator: () -> String) {
        this.userAgentGenerator = userAgentGenerator
    }

}


