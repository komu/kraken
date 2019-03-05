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

package dev.komu.kraken.model.creature

import dev.komu.kraken.model.Game
import dev.komu.kraken.model.common.Attack
import dev.komu.kraken.model.item.weapon.NaturalWeapon
import dev.komu.kraken.model.region.Cell

open class Monster(name: String): Creature(name) {

    private var lastKnownPlayerPosition: Cell? = null

    var naturalWeapon: Attack = NaturalWeapon("hit", "0", "randint(1, 3)")

    override fun onTick(game: Game) {
        val player = game.player
        val seesPlayer = seesCreature(player)

        if (seesPlayer)
            lastKnownPlayerPosition = player.cell

        if (friendly) {
            moveRandomly()
            return
        }

        if (seesPlayer) {
            if (isAdjacentToCreature(player)) {
                game.attack(this, player)
            } else if (!immobile)
                moveTowards(player.cell)

        } else {
            if (cell == lastKnownPlayerPosition)
                lastKnownPlayerPosition = null

            if (!immobile) {
                val playerPosition = lastKnownPlayerPosition
                if (playerPosition != null)
                    moveTowards(playerPosition)
                else
                    moveRandomly()
            }

        }
    }

    override val naturalAttack: Attack
        get() = naturalWeapon
}
