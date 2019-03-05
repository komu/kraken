package dev.komu.kraken.utils

fun Iterable<String>.maxLength(): Int = maxBy { it.length }?.length ?: 0
