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
import dev.komu.kraken.utils.exp.Expression

class CreatureDefinition<out T : Creature>(val name: String, override val level: Int, val createCreature: () -> T) : dev.komu.kraken.definitions.ObjectDefinition<T>() {

    var swarmSize = Expression.constant(1)

    var instantiable = true

    fun createSwarm(): Collection<T> {
        val swarm = mutableListOf<T>()

        repeat(swarmSize.evaluate()) {
            swarm.add(create())
        }

        return swarm
    }

    override fun create(): T = createCreature()

    override fun toString() = "CreatureDefinition [name=$name]"
}
