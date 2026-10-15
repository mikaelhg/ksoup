package io.mikael.ksoup

/**
 * Prevents implicit access to the receiver of an outer DSL scope from inside a nested one.
 */
@DslMarker
@Target(AnnotationTarget.CLASS)
annotation class KSoupDsl
