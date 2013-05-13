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

package net.wanhack.service.creature

import net.wanhack.model.creature.Creature
import net.wanhack.service.ServiceProvider
import net.wanhack.service.config.ObjectDefinition
import net.wanhack.service.config.ObjectFactory
import java.util.ArrayList
import java.util.Random

class CreatureService {

    private val random = Random()

    fun randomSwarm(regionLevel: Int, playerLevel: Int): Collection<Creature> {
        val minMonsterLevel = regionLevel / 6
        val maxMonsterLevel = (regionLevel + playerLevel) / 2
        return randomSwarmBetween(minMonsterLevel, maxMonsterLevel)
    }

    fun randomSwarmBetween(minLevel: Int, maxLevel: Int): Collection<Creature> {
        val defs = objectFactory.getAvailableDefinitionsForClass(javaClass<Creature>())
        val def = random(defs, minLevel, maxLevel)
        val swarmSize = def.swarmSize()
        val swarm = ArrayList<Creature>(swarmSize)

        swarmSize.times {
            swarm.add(objectFactory.create(javaClass<Creature>(), def.name))
        }

        return swarm
    }

    val objectFactory: ObjectFactory
        get() = ServiceProvider.objectFactory

    private fun random(defs: List<ObjectDefinition>, minLevel: Int, maxLevel: Int): ObjectDefinition {
        var probabilitySum: Int = 0
        val probs = ArrayList<DefProbability>(defs.size)

        for (od in defs) {
            val level = od.level
            if (level == null || (level >= minLevel && level <= maxLevel)) {
                val probability = od.probability ?: 100
                probs.add(DefProbability(od, probability, level))
                probabilitySum += probability
            }

        }

        var item = random.nextInt(probabilitySum)
        for (dp in probs) {
            val level = dp.level
            if (level == null || (level >= minLevel && level <= maxLevel)) {
                if (item < dp.probability)
                    return dp.def

                item -= dp.probability
            }
        }
        throw RuntimeException("could not randomize definition")
    }

    class object {
        val instance = CreatureService()
    }

    class DefProbability(val def: ObjectDefinition, val probability: Int, val level: Int?)
}
