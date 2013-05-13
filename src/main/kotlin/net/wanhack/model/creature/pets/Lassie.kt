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

package net.wanhack.model.creature.pets

import net.wanhack.model.Game
import net.wanhack.model.creature.Creature
import net.wanhack.model.region.Cell

class Lassie(name: String): Pet(name) {

    {
        weight = 25
        letter = 'C'
    }

    override fun talk(target: Creature) {
        target.message("$name barks.")
    }

    override fun onTick(game: Game) {
        val escape = findEscapeStairs()
        if (escape != null) {
            if (escape == cell) {
                hitPoints = 0
                removeFromGame()
                game.message("%s went home.", name)
            } else {
                val ok = moveTowards(escape)
                if (!ok) {
                    super.onTick(game)
                }
            }
        } else {
            super.onTick(game)
        }
    }

    private fun findEscapeStairs(): Cell? {
        for (cell in region!!) {
            val target = cell.getJumpTarget(true)
            if (target != null && target.isExit)
                return cell
        }
        return null
    }
}
