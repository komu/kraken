package dev.komu.kraken.utils

import java.io.FileNotFoundException
import java.util.*

object ResourceLoader {

    private fun openStream(path: String) =
        javaClass.getResourceAsStream(path) ?: throw FileNotFoundException("classpath:$path")

    fun readLines(path: String): List<String> = openStream(path).reader().use { it.readLines() }

    fun readProperties(path: String): Properties =
        openStream(path).use { s ->
            Properties().apply {
                load(s)
            }
        }
}
