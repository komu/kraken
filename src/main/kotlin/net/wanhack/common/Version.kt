/*
 * Copyright 2013 The Wanhack Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.wanhack.common

import java.io.FileNotFoundException
import java.io.IOException
import java.util.Properties

class Version(val version: String, val revision: String) {

    class object {
        private var myInstance: Version? = null

        public val fullVersion: String
            get() {
                val version = instance
                return if (version.revision != "")
                    "${version.version} (r${version.revision})"
                else
                    version.version
            }

        public val version: String
            get() = instance.version

        public val revision: String
            get() = instance.revision

        private val instance: Version
            get() {
                try {
                    if (myInstance == null)
                        myInstance = loadVersion()

                    return myInstance!!
                } catch (e: IOException) {
                    return Version("unknown", "")
                }
            }

        private fun loadVersion(): Version =
            openResource("/version.properties").use { inputStream ->
                val properties = Properties()
                properties.load(inputStream)

                val version = properties.getProperty("version") ?: "unknown"
                val revision = properties.getProperty("revision") ?: ""

                Version(version, revision)
            }

        private fun openResource(name: String) =
            javaClass<Version>().getResourceAsStream(name) ?: throw FileNotFoundException("resource:" + name)
    }
}
