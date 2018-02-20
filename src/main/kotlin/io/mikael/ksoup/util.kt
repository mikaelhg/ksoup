package io.mikael.ksoup

internal inline fun <reified T : Any> T.getClassLogger() : java.util.logging.Logger
        = java.util.logging.Logger.getLogger(T::class.java.name)
