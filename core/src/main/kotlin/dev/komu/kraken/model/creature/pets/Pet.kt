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

package dev.komu.kraken.model.creature.pets

import dev.komu.kraken.model.Game
import dev.komu.kraken.model.common.Attack
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.item.weapon.NaturalWeapon
import dev.komu.kraken.model.region.Cell
import dev.komu.kraken.utils.Probability

abstract class Pet(name: String): Creature(name) {

    private var lastKnownPlayerPosition: Cell? = null
    private var naturalWeapon = NaturalWeapon("bite", "1", "randint(3, 7)")

    init {
        friendly = true
    }

    override fun onTick(game: Game) {
        val player = game.player

        val enemy = adjacentCreatures.find { !it.isPlayer }
        if (enemy != null) {
            game.attack(this, enemy)

        } else if (seesCreature(player)) {
            lastKnownPlayerPosition = player.cell
            if (isAdjacentToCreature(player) || Probability.check(50))
                moveRandomly()
            else
                moveTowards(player.cell)

        } else {
            if (cell == lastKnownPlayerPosition)
                lastKnownPlayerPosition = null

            val known = lastKnownPlayerPosition
            if (known != null)
                moveTowards(known)
            else
                moveRandomly()
        }
    }

    override val naturalAttack: Attack
        get() = naturalWeapon
}
