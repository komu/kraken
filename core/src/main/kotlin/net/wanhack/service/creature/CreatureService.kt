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
import net.wanhack.definitions.weightedRandom
import net.wanhack.definitions.CreatureDefinition
import net.wanhack.definitions.betweenLevels

class CreatureService {

    fun randomSwarm(regionLevel: Int, playerLevel: Int): Collection<Creature> {
        val minLevel = regionLevel / 6
        val maxLevel = (regionLevel + playerLevel) / 2

        return creatures.betweenLevels(minLevel, maxLevel).weightedRandom().createSwarm()
    }

    private val creatures: Collection<CreatureDefinition<*>>
        get() = ServiceProvider.objectFactory.instantiableCreatures

    class object {
        val instance = CreatureService()
    }
}
