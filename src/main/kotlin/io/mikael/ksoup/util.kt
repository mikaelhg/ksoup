package io.mikael.ksoup

import java.util.logging.Logger

internal fun getLogger(klass: Class<*>) = Logger.getLogger(klass.name)
