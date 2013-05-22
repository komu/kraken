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
import java.util.ArrayList
import net.wanhack.utils.exp.Expression
import net.wanhack.model.creature.Creature
import net.wanhack.utils.RandomUtils

abstract class Definitions {

    val itemDefinitions = ArrayList<ItemDefinition<*>>()
    val creatureDefinitions = ArrayList<CreatureDefinition<*>>()

    fun item<T : Item>(name: String,
                       level: Int? = null,
                       probability: Int? = null,
                       maximumInstances: Int? = null,
                       create: () -> T): ItemDefinition<T> {
        val def = ItemDefinition<T>(name, create)

        if (level != null)
            def.level = level

        if (probability != null)
            def.probability = probability

        if (maximumInstances != null)
            def.maximumInstances = maximumInstances

        itemDefinitions.add(def)
        return def
    }

    fun creature<T : Creature>(name: String,
                               level: Int,
                               probability: Int? = null,
                               swarmSize: Expression? = null,
                               create: () -> T): CreatureDefinition<T> {
        val def = CreatureDefinition<T>(name, level, create)

        if (probability != null)
            def.probability = probability

        if (swarmSize != null)
            def.swarmSize = swarmSize

        creatureDefinitions.add(def)
        return def
    }

    fun exp(exp: String) = Expression.parse(exp)

    fun randint(min: Int, max: Int) = RandomUtils.randomInt(min, max)

    fun <T : Any> T.init(callback: T.() -> Unit): T {
        callback()
        return this
    }
}
