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

package net.wanhack.definitions

import net.wanhack.model.item.Item
import net.wanhack.service.config.ObjectDefinition
import java.util.ArrayList
import net.wanhack.utils.exp.Expression
import net.wanhack.model.creature.Creature
import net.wanhack.utils.RandomUtils

abstract class Definitions {

    val definitions = ArrayList<ObjectDefinition<*>>()

    fun item<T : Item>(name: String,
                       parent: ObjectDefinition<in T>? = null,
                       isAbstract: Boolean = false,
                       level: Int? = null,
                       objectClass: Class<T>? = null,
                       probability: Int? = null,
                       maximumInstances: Int? = null,
                       init: T.() -> Unit): ObjectDefinition<T> {
        val def = ObjectDefinition<T>(name)
        def.objectClass = objectClass
        def.isAbstract = isAbstract
        def.parent = parent
        def.probability = probability
        def.level = level
        if (maximumInstances != null)
            def.maximumInstances = maximumInstances
        def.initHook = init

        definitions.add(def)
        return def
    }

    fun creature<T : Creature>(name: String,
                               parent: ObjectDefinition<in T>? = null,
                               isAbstract: Boolean = false,
                               level: Int? = null,
                               objectClass: Class<T>? = null,
                               probability: Int? = null,
                               swarmSize: Expression? = null,
                               init: T.() -> Unit): ObjectDefinition<T> {
        val def = ObjectDefinition<T>(name)
        def.objectClass = objectClass
        def.isAbstract = isAbstract
        def.parent = parent
        def.probability = probability
        def.level = level
        def.initHook = init
        if (swarmSize != null)
            def.swarmSize = swarmSize

        definitions.add(def)
        return def
    }

    fun exp(exp: String) = Expression.parse(exp)

    fun randint(min: Int, max: Int) = RandomUtils.randomInt(min, max)
}
