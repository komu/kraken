/*
 * Copyright 2013 The Releasers of Kraken
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

package dev.komu.kraken.definitions

import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.item.Item
import dev.komu.kraken.utils.exp.Expression

abstract class Definitions {

    val itemDefinitions = mutableListOf<dev.komu.kraken.definitions.ItemDefinition<*>>()
    val creatureDefinitions = mutableListOf<dev.komu.kraken.definitions.CreatureDefinition<*>>()

    fun <T : Item> item(name: String,
                        level: Int? = null,
                        probability: Int? = null,
                        maximumInstances: Int? = null,
                        create: () -> T): dev.komu.kraken.definitions.ItemDefinition<T> {
        val def = dev.komu.kraken.definitions.ItemDefinition(name, create)

        if (level != null)
            def.level = level

        if (probability != null)
            def.probability = probability

        if (maximumInstances != null)
            def.maximumInstances = maximumInstances

        itemDefinitions.add(def)
        return def
    }

    fun <T : Creature> creature(name: String,
                                level: Int,
                                probability: Int? = null,
                                swarmSize: Expression? = null,
                                create: () -> T): dev.komu.kraken.definitions.CreatureDefinition<T> {
        val def = dev.komu.kraken.definitions.CreatureDefinition(name, level, create)

        if (probability != null)
            def.probability = probability

        if (swarmSize != null)
            def.swarmSize = swarmSize

        creatureDefinitions.add(def)
        return def
    }

    fun exp(exp: String) = Expression.parse(exp)
}
