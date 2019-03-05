package dev.komu.kraken.utils

import java.security.AccessControlException

object SystemAccess {

    /**
     * Returns value named system property.
     *
     * If the program does not have the right to access given system property (i.e. in WebStart environment),
     * or if the property is not defined, defaultValue is returned.
     */
    fun getSystemProperty(name: String, defaultValue: String?): String? =
        try {
            System.getProperty(name, defaultValue)
        } catch (e: AccessControlException) {
            defaultValue
        }

    /**
     * Returns value named system property.
     *
     * If the program does not have the right to access given system property (i.e. in WebStart environment),
     * null is returned.
     */
    fun getSystemProperty(name: String): String? =
        getSystemProperty(name, null)
}
