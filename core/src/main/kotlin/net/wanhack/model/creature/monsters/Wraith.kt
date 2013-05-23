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

package net.wanhack.model.creature.monsters

import net.wanhack.model.creature.Monster
import net.wanhack.utils.Probability
import net.wanhack.utils.RandomUtils
import net.wanhack.definitions.Items
import net.wanhack.model.common.Color

class Wraith(name: String): Monster(name) {

    {
        letter = 'W'
        color = Color.BLACK
        canUseDoors = true
    }

    override fun createCorpse() =
        if (Probability.check(10)) {
            val essence = Items.wraithEssence.create()
            essence.healingEffect = RandomUtils.rollDie(killExperience)
            essence
        } else {
            val rags = Items.oldRags.create()
            rags.color = color
            rags
        }
}
