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

package net.wanhack.model.creature

import net.wanhack.common.MessageTarget
import net.wanhack.model.Game
import net.wanhack.model.common.Actor
import net.wanhack.model.common.Attack
import net.wanhack.model.common.Direction
import net.wanhack.model.item.Item
import net.wanhack.model.item.armor.Armoring
import net.wanhack.model.item.food.Corpse
import net.wanhack.model.item.food.Taste
import net.wanhack.model.item.weapon.Weapon
import net.wanhack.model.item.weapon.WeaponClass
import net.wanhack.model.region.Cell
import net.wanhack.model.region.Region
import net.wanhack.utils.Probability
import net.wanhack.utils.RandomUtils
import net.wanhack.utils.exp.Expression
import java.lang.Math.max
import java.util.*
import net.wanhack.utils.collections.filterByType
import net.wanhack.model.common.Directions
import net.wanhack.utils.collections.toOption
import net.wanhack.model.common.Color

abstract class Creature(var name: String): Actor, MessageTarget {

    var cellOrNull: Cell? = null
        set(cell: Cell?) {
            val oldValue = $cellOrNull
            if (oldValue != null)
                oldValue.creature = null

            $cellOrNull = cell

            if (cell != null)
                cell.creature = this
        }

    var cell: Cell
        get() = cellOrNull ?: throw NullPointerException("no current cell for $this")
        set(cell: Cell) = cellOrNull = cell

    var letter = '\u0000'
        get() = if ($letter == '\u0000') name[0] else $letter

    var color = Color.GRAY
    open var hitPoints = 1
    var friendly = false
    var immobile = false
    var corporeal = true
    var omniscient = false
    var level = 1
    var killExperience = -1
        get() = if ($killExperience != -1) $killExperience else level * level

    var hitBonus = 0

    var armorClass = 10
        get() = $armorClass - armoring.totalArmorBonus

    var luck: Int = 0
    open var tickRate = 100
    var weight = 50 * 1000
    var canUseDoors: Boolean = false
    var corpsePoisonousness = Expression.parse("randint(1, 3)")
    var wieldedWeapon: Weapon? = null
    var strength = RandomUtils.rollDie(10, 10)
    var charisma = RandomUtils.rollDie(10, 10)
    var taste = Taste.CHICKEN
    val armoring = Armoring()
    val inventoryItems = HashSet<Item>()

    /** Numbers of ticks that this creature is paralyzed for */
    var paralyzedTicks = 0
        set(ticks) {
            $paralyzedTicks = max(0, ticks)
        }

    val game: Game
        get() = region.world.game

    val weightOfCarriedItems: Int
        get() {
            var weight = wieldedWeapon?.weight ?: 0

            for (armor in armoring)
                weight += armor.weight

            for (item in inventoryItems)
                weight += item.weight

            return weight
        }

    fun getInventoryItems<T: Item>(cl: Class<T>) =
        inventoryItems.filterByType(cl)

    open fun getProficiency(weaponClass: WeaponClass): Int = 0

    open val isPlayer: Boolean
        get() = false

    val region: Region
        get() = cell.region

    fun seesCreature(creature: Creature) =
        canSee(creature.cell)

    fun isAdjacentToCreature(creature: Creature) =
        cell.isAdjacent(creature.cell)

    override val destroyed: Boolean
        get() = !alive

    val alive: Boolean
        get() = hitPoints > 0 && cellOrNull != null

    val paralyzed: Boolean
        get() = paralyzedTicks > 0

    override fun act(game: Game): Int {
        if (paralyzed) {
            paralyzedTicks -= tickRate
            return tickRate
        }
        onTick(game)
        return tickRate
    }

    protected fun moveTowards(targetCell: Cell): Boolean {
        val searcher = CreatureShortestPathSearcher(this)
        val first = searcher.findFirstCellOnShortestPath(cell, targetCell)
        if (first != null && canMoveTo(first)) {
            cell = first
            return true
        } else {
            return false
        }
    }

    protected fun move(direction: Direction): Boolean {
        val target = cell.getCellTowards(direction)
        if (canMoveTo(target)) {
            target.enter(this)
            return true

        } else if (target.isClosedDoor() && canUseDoors) {
            target.openDoor(this)
            return true
        } else {
            return false
        }
    }

    protected fun canMoveTo(cell: Cell): Boolean =
        cell.canMoveInto(corporeal)

    protected fun moveRandomly() {
        if (Probability.check(75))
            move(Directions.randomDirection())
    }

    protected abstract fun onTick(game: Game)

    open fun canSee(target: Cell): Boolean {
        if (omniscient)
            return true

        return calculateCanSee(target)
    }

    protected fun calculateCanSee(target: Cell): Boolean =
        cell.hasLineOfSight(target)

    val adjacentCreatures: Collection<Creature>
        get() = cell.adjacentCells.flatMap { it.creature.toOption() }

    fun toString() = "$name [hp=$hitPoints]"

    open fun onAttackedBy(attacker: Creature) {
        if (attacker.isPlayer)
            friendly = false
    }

    open fun onSuccessfulHit(target: Creature, weapon: Attack) {
    }

    open fun onKilledCreature(target: Creature) {
    }

    open fun takeDamage(points: Int, attacker: Creature) {
        hitPoints = Math.max(0, hitPoints - points)
    }

    open fun talk(target: Creature) {
        target.say(this, "Hrmph.")
    }

    open val attack: Attack
        get() = wieldedWeapon ?: naturalAttack

    abstract val naturalAttack: Attack

    open fun you(): String = name

    open fun You(): String = you().capitalize()

    open fun verb(verb: String): String =
        if (verb.endsWith("s")) "${verb}es" else "${verb}s"

    open fun createCorpse(): Item? {
        if (!corporeal)
            return null

        val corpse = Corpse("$name corpse")
        corpse.weight = weight
        corpse.color = color
        corpse.level = level
        corpse.poisonDamage = corpsePoisonousness
        corpse.effectiveness = Math.max((0.05 * weight).toInt(), 800)
        corpse.taste = taste
        return corpse
    }

    open fun die(killer: String) {
        hitPoints = 0
        cell.items.addAll(inventoryItems)
        inventoryItems.clear()

        val weapon = wieldedWeapon
        if (weapon != null) {
            cell.items.add(weapon)
            wieldedWeapon = null
        }

        cell.items.addAll(armoring.removeAllArmors())

        val corpse = createCorpse()
        if (corpse != null)
            cell.items.add(corpse)

        removeFromGame()
    }

    protected fun removeFromGame() {
        cellOrNull = null
    }

    val lighting: Int
        get() {
            var total = 0
            for (item in inventoryItems)
                total += item.lighting
            return total
        }

    override fun message(pattern: String, vararg args: Any?) {
    }

    open fun say(talker: Creature, message: String, vararg args: Any?) {
    }

    open fun ask(defaultValue: Boolean, question: String, vararg args: Any?): Boolean {
        return defaultValue
    }
}
