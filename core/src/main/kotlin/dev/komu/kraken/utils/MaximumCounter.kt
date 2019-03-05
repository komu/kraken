package dev.komu.kraken.utils

class MaximumCounter<T : Comparable<T>>(private var current: T) {

    val value: T
        get() = current

    fun update(newValue: T) {
        if (newValue > current)
            current = newValue
    }

    override fun toString() = value.toString()
}
