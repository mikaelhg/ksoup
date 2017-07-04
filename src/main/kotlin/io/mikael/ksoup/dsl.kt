package io.mikael.ksoup

/**
 * Invoke the methods on this main DSL class to fetch data using JSoup.
 */
object KSoup {

    fun <V : Any> simple(init: SimpleExtractor<V>.() -> Unit) =
            SimpleExtractor<V>().apply(init)

    fun <V : Any> extract(init: SimpleExtractor<V>.() -> Unit) =
            simple(init).extract()

}

/**
 * Don't know yet if we'll be keeping this, or just using the ExtractorBase.
 * Depends on how the more complicated use cases pan out.
 */
interface Extractor<out V> {
    fun extract(): V
}

/**
 * We'll base the simple, detail page and multipage extractors on this.
 */
abstract class ExtractorBase<V : Any> : Extractor<V>, HttpClient() {

    internal lateinit var instanceGenerator: () -> V

    internal lateinit var urlGenerator: () -> String

    var url: String
        get() = urlGenerator()
        set(value) { this.urlGenerator = { value } }

    protected fun document() = get(urlGenerator())

    /**
     * Pass me a generator function for your result type.
     * I'll make you one of these, so you can stuff it with information from the page.
     */
    fun result(generator: () -> V) {
        this.instanceGenerator = generator
    }

    /**
     * Pass me an instance, I'll fill it in from the page.
     */
    fun result(instance: V) = result { instance }

}
