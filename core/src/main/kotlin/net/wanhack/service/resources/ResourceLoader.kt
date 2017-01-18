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

package net.wanhack.service.resources

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
