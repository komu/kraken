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

package net.wanhack.service.config

import java.util.HashMap
import net.wanhack.definitions.Definitions
import net.wanhack.utils.collections.toOption

class ObjectFactory {
    private val definitions = HashMap<String, ObjectDefinition<*>>()

    fun addDefinition(definition: ObjectDefinition<*>) {
        definitions[definition.name] = definition
    }

    fun addDefinitions(definitions: Definitions) {
        for (definition in definitions.definitions)
            addDefinition(definition)
    }

    fun create<T : Any>(objectClass: Class<T>, name: String): T =
        getDefinition(name).cast(objectClass)!!.create()

    fun getAvailableDefinitionsForClass<T : Any>(cl: Class<T>): List<ObjectDefinition<T>> =
        definitions.values().flatMap { it -> it.cast(cl).toOption() }

    private fun getDefinition(name: String) =
        definitions[name] ?: throw ConfigurationException("No such object <$name>")

    fun toString() = definitions.toString()
}
