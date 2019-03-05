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

package dev.komu.kraken.model.creature.monsters

import dev.komu.kraken.model.common.Color
import dev.komu.kraken.model.creature.Monster
import dev.komu.kraken.utils.Probability
import dev.komu.kraken.utils.rollDie

class Wraith(name: String): Monster(name) {

    init {
        letter = 'W'
        color = Color.BLACK
        canUseDoors = true
    }

    override fun createCorpse() =
        if (Probability.check(10)) {
            val essence = dev.komu.kraken.definitions.Items.wraithEssence.create()
            essence.healingEffect = rollDie(killExperience)
            essence
        } else {
            val rags = dev.komu.kraken.definitions.Items.oldRags.create()
            rags.color = color
            rags
        }
}
