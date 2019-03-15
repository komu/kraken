package dev.komu.kraken.desktop.common

import dev.komu.kraken.utils.ResourceLoader

class Version(val version: String) {

    companion object {
        private val instance = Version(
            ResourceLoader.readProperties("/version.properties").getProperty(
                "version",
                "unknown"
            )
        )

        val fullVersion: String
            get() = instance.version
    }
}
