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

import dev.komu.kraken.model.Game
import dev.komu.kraken.model.common.Color
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.creature.Monster
import dev.komu.kraken.model.item.weapon.NaturalWeapon
import dev.komu.kraken.utils.randomEnum
import dev.komu.kraken.utils.randomInt

class BugsBunny: Monster("Bugs Bunny") {

    init {
        level = 20
        hitPoints = randomInt(100, 200)
        letter = 'r'
        color = Color.WHITE
        luck = 2
        naturalWeapon = NaturalWeapon("hit", "10", "randint(4,10)")
        killExperience = 450
        armorClass = 0
        tickRate = 50
        weight = 4500
    }

    override fun talk(target: Creature) {
        target.say(this, "What's up, Doc?")
    }

    override fun onTick(game: Game) {
        moveRandomly()

        if (seesCreature(game.player)) {
            val direction = randomEnum<dev.komu.kraken.common.Direction>()
            game.movePlayer(direction)
        }
    }
}
