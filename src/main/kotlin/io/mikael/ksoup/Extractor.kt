package io.mikael.ksoup

import io.mikael.ksoup.net.WebSupport

@KSoupDsl
abstract class Extractor<V : Any> : WebSupport() {

    internal lateinit var instanceGenerator: () -> V

    internal lateinit var urlGenerator: () -> String

    var url: String
        get() = urlGenerator()
        set(value) { this.urlGenerator = { value } }

    abstract fun extract(): V

    protected fun document() = get(url)

    /**
     * Pass me a generator function for your result type.
     * I'll make you one of these, so you can stuff it with information from the page.
     */
    fun result(generator: () -> V) {
        this.instanceGenerator = generator
    }

    /**
     * Pass me an instance, and I'll fill it in with data from the page.
     */
    fun result(instance: V) = result { instance }

}
