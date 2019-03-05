package dev.komu.kraken.service.resources

import java.io.FileNotFoundException
import java.util.*

object ResourceLoader {

    fun openStream(path: String) = javaClass.getResourceAsStream(path) ?: throw FileNotFoundException("classpath:$path")

    fun openReader(path: String) = openStream(path).reader(Charsets.UTF_8)

    fun readLines(path: String): List<String> = openReader(path).use { it.readLines() }

    fun readProperties(path: String): Properties =
            openStream(path).use { s ->
                val properties = Properties()
                properties.load(s)
                return properties
            }
}
