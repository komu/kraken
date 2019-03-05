package dev.komu.kraken.common

import dev.komu.kraken.service.resources.ResourceLoader

class Version(val version: String) {

    companion object {
        private val instance = ResourceLoader.readProperties("/version.properties").let { properties ->
            Version(properties.getProperty("version", "unknown"))
        }

        val fullVersion: String
            get() = instance.version

        val version: String
            get() = instance.version
    }
}
