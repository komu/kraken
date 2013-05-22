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

import net.wanhack.utils.exp.Expression
import net.wanhack.model.creature.Creature

class CreatureDefinition<T : Creature>(val name: String, override val level: Int, val createCreature: () -> T) : ObjectDefinition<T>() {

    var swarmSize = Expression.constant(1)

    val instantiable: Boolean
        get() = true

    fun createSwarm(): Collection<T> {
        val swarm = listBuilder<T>()

        swarmSize.evaluate().times {
            swarm.add(create())
        }

        return swarm.build()
    }

    override fun create(): T {
        return createCreature()
    }

    fun toString() = "CreatureDefinition [name=$name]"
}
