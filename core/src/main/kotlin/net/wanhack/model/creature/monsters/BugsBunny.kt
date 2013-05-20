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

import java.awt.Color
import net.wanhack.model.Game
import net.wanhack.model.common.Direction
import net.wanhack.model.creature.Creature
import net.wanhack.model.creature.Monster
import net.wanhack.utils.RandomUtils

class BugsBunny(name: String): Monster(name) {

    {
        level = 3
        hitPoints = 1
        letter = 'r'
        color = Color.WHITE
        tickRate = 40
    }

    override fun talk(target: Creature) {
        target.say(this, "What's up, Doc?")
    }

    override fun onTick(game: Game) {
        moveRandomly()

        if (seesCreature(game.player)) {
            val direction = RandomUtils.randomEnum(javaClass<Direction>())
            game.movePlayer(direction)
        }
    }
}
