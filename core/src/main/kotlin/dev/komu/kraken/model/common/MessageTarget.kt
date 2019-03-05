package dev.komu.kraken.model.common

interface MessageTarget {
    fun message(pattern: String, vararg args: Any?)
}
