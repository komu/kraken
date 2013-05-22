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
import net.wanhack.utils.exp.Expression
import net.wanhack.utils.logger

class ObjectDefinition<T>(val name: String) {

    var isAbstract = false
    val attributes = HashMap<String, Any>()
    var objectClass: Class<out T>? = null
        get() = $objectClass ?: parent?.objectClass as Class<out T>
        set(objectClass: Class<out T>?) = $objectClass = objectClass

    var parent: ObjectDefinition<in T>? = null
    var swarmSize = Expression.constant(1)

    var probability: Int? = null
        get() = $probability ?: parent?.probability
        set(probability: Int?) = $probability = probability

    var level: Int? = null
        get() = $level ?: parent?.level
        set(level: Int?) = $level = level

    var maximumInstances = Integer.MAX_VALUE
    var createdInstances = 0
    var initHook: T.() -> Unit = { }

    private val log = javaClass.logger()

    fun swarmSize(): Int =
        swarmSize.evaluate()

    fun isInstantiable(cl: Class<*>): Boolean {
        val oc = objectClass
        return oc != null && cl.isAssignableFrom(oc) && !isAbstract && createdInstances < maximumInstances
    }

    private fun getAttributes(): Map<String, Any> {
        if (parent == null)
            return attributes
        else {
            val result = HashMap<String, Any>(parent!!.getAttributes())
            result.putAll(attributes)
            return result
        }
    }

    fun cast<K>(cl: Class<K>): ObjectDefinition<K>? =
        if (isInstantiable(cl))
            this as ObjectDefinition<K> else null

    fun createSwarm(): Collection<T> {
        val swarm = listBuilder<T>()

        swarmSize().times {
            swarm.add(create())
        }

        return swarm.build()
    }

    fun create(): T {
        if (isAbstract)
            throw ConfigurationException("Can't instantiate abstract definition <$name>")

        try {
            val oc = objectClass ?: throw ConfigurationException("object class not set for <$name>")
            val ctor = oc.getConstructor(javaClass<String>())

            val obj = ctor.newInstance(name)!!

            initialize(obj)

            createdInstances++
            return obj

        } catch (e: Exception) {
            throw ConfigurationException("Can't construct object <$name>", e)
        }
    }

    fun initialize(obj: T) {
        parent?.initialize(obj)
        obj.initHook()
    }

    fun toString() = "ObjectDefinition [name=$name]"
}
