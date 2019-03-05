package dev.komu.kraken.utils

import java.util.logging.Logger

fun Class<*>.logger(): Logger =
    Logger.getLogger(name)
