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
import net.wanhack.service.ServiceProvider
import net.wanhack.utils.Probability
import net.wanhack.utils.RandomUtils
import net.wanhack.utils.exp.Expression
import java.awt.*
import java.util.*

abstract class Creature(var name: String): Actor, MessageTarget {

    var cellOrNull: Cell? = null
        set(cell: Cell?) {
            if ($cellOrNull != null)
                $cellOrNull!!.creature = null

            $cellOrNull = cell

            if ($cellOrNull != null)
                $cellOrNull!!.creature = this
        }

    var cell: Cell
        get() = cellOrNull!!
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

    fun getInventoryItems<T: Item>(cl: Class<T>): Collection<T> {
        val result = listBuilder<T>()
        for (item in inventoryItems)
            if (cl.isInstance(item))
                result.add(cl.cast(item)!!)

        return result.build()
    }

    fun clearInventory() {
        inventoryItems.clear()
    }

    fun addItemToInventory(item: Item) {
        inventoryItems.add(item)
    }

    fun removeItemFromInventory(item: Item) {
        inventoryItems.remove(item)
    }

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
        get() = !isAlive

    val isAlive: Boolean
        get() = hitPoints > 0 && cellOrNull != null

    override fun act(game: Game): Int {
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
            move(RandomUtils.randomEnum(javaClass<Direction>()))
    }

    protected abstract fun onTick(game: Game)

    open fun canSee(target: Cell): Boolean {
        if (omniscient)
            return true

        return calculateCanSee(target)
    }

    protected fun calculateCanSee(target: Cell): Boolean =
        cell.getCellsBetween(target).all { it.canSeeThrough() }

    fun getAdjacentCreatures(): Set<Creature> {
        val adjacent = HashSet<Creature>()
        for (c in cell.getAdjacentCells()) {
            val creature = c.creature
            if (creature != null)
                adjacent.add(creature)
        }
        return adjacent
    }

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

    open fun You(): String {
        val name = you()
        return "${Character.toUpperCase(name[0])}${name.substring(1)}"
    }

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
        cell.addItems(inventoryItems)
        clearInventory()

        val weapon = wieldedWeapon
        if (weapon != null) {
            cell.addItem(weapon)
            wieldedWeapon = null
        }

        cell.addItems(armoring.removeAllArmors())

        val corpse = createCorpse()
        println("corpse: $corpse")
        if (corpse != null)
            cell.addItem(corpse)

        removeFromGame()
    }

    protected fun removeFromGame() {
        cellOrNull = null
    }

    protected fun createItem<T>(cl: Class<T>, name: String): T =
        ServiceProvider.objectFactory.create(cl, name)

    val lighting: Int
        get() {
            var effectiveness = 0
            for (item in inventoryItems)
                effectiveness += item.lighting
            return effectiveness
        }

    override fun message(pattern: String, vararg args: Any?) {
    }

    open fun say(talker: Creature, message: String, vararg args: Any?) {
    }

    open fun ask(defaultValue: Boolean, question: String, vararg args: Any?): Boolean {
        return defaultValue
    }
}
