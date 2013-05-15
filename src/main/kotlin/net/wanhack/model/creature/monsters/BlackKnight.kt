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
import net.wanhack.model.creature.Creature
import net.wanhack.model.creature.Monster
import net.wanhack.model.item.Item
import net.wanhack.model.item.weapon.NaturalWeapon
import net.wanhack.model.region.Cell
import net.wanhack.utils.collections.shuffled
import net.wanhack.utils.collections.randomElement

class BlackKnight(name: String): Monster(name) {

    private var lastKnownPlayerPosition: Cell? = null
    private val bite = NaturalWeapon("bite", "1", "randint(0, 1)")
    private var hasBeenFighting = false
    private var maxHitPoints = 1;

    override var hitPoints: Int = 5
        set(hitPoints: Int) {
            super.hitPoints = hitPoints
            maxHitPoints = Math.max(maxHitPoints, hitPoints)
        }

    {
        level = 3
        tickRate = 50
        letter = 'p'
        color = Color.BLACK
        armorClass = 7
    }

    override fun talk(target: Creature)  {
        val percentage = hitPointPercentage
        val yells = when {
            percentage >= 80 -> HEALTHY_YELLS
            percentage >= 60 -> ONE_ARMED_YELLS
            percentage >= 40 -> ARMLESS_YELLS
            percentage >= 20 -> ONE_LEGGED_YELLS
            else             -> TORSO_YELLS
        }

        target.say(this, yells.randomElement())
    }

    private val hitPointPercentage: Int
        get() = 100 * hitPoints / maxHitPoints

    private val fullyCrippled: Boolean
        get() = hitPointPercentage < 20

    override fun onTick(game: Game) {
        val player = game.player

        val isAdjacent = isAdjacentToCreature(player)

        if (hasBeenFighting && !isAdjacent) {
            player.say(this, PLAYER_FLEEING_YELLS.randomElement())
            hasBeenFighting = false

        } else if (isAdjacent) {
            talk(player)
        }

        if (isAdjacent) {
            game.attack(this, player)
            hasBeenFighting = true
        } else if (!fullyCrippled) {
            if (seesCreature(player)) {
                lastKnownPlayerPosition = player.cell
                moveTowards(player.cell)
            } else {
                if (cell == lastKnownPlayerPosition)
                    lastKnownPlayerPosition = null

                val knownPosition = lastKnownPlayerPosition
                if (knownPosition != null)
                    moveTowards(knownPosition)
            }
        }
    }

    override val naturalAttack = bite

    override fun takeDamage(points: Int, attacker: Creature) {
        hasBeenFighting = true
        hitPoints = Math.max(1, hitPoints - points)
        if (fullyCrippled)
            return

        tickRate *= 2
        if (fullyCrippled) {
            attacker.message("The Black Knight is crippled!")
            val weapon = wieldedWeapon
            if (weapon != null) {
                wieldedWeapon != null
                dropToAdjacentCell(weapon)
            }
        } else {
            attacker.message("The Black Knight loses a limb.")
        }
    }

    private fun dropToAdjacentCell(item: Item) {
        val cells = cell.getAdjacentCells().shuffled()
        for (cell in cells) {
            if (cell.canDropItemToCell()) {
                cell.addItem(item)
                return
            }
        }
        cell.addItem(item)
    }

    class object {
        private val HEALTHY_YELLS = listOf("None shall pass.", "I move for no man.", "Aaaagh!")
        private val ONE_ARMED_YELLS = listOf("Tis but a scratch.", "I've had worse.", "Come on, you pansy!")
        private val ARMLESS_YELLS = listOf("Come on, then.", "Have at you!")
        private val ONE_LEGGED_YELLS = listOf("Right. I'll do you for that!", "I'm invincible!")
        private val TORSO_YELLS = listOf("Oh. Oh, I see. Running away, eh?", "You yellow bastard!", "Come back here and take what's coming to you.", "I'll bite your legs off!")
        private val PLAYER_FLEEING_YELLS = listOf("Oh, had enough, eh?", "Just a flesh wound.", "Chicken! Chickennn!")
    }
}
