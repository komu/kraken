package dev.komu.kraken.model.creature

import dev.komu.kraken.model.Energy
import dev.komu.kraken.model.Game
import dev.komu.kraken.model.Inventory
import dev.komu.kraken.model.actions.Action
import dev.komu.kraken.model.actions.MoveAction
import dev.komu.kraken.model.common.Attack
import dev.komu.kraken.model.common.Color
import dev.komu.kraken.model.common.MessageTarget
import dev.komu.kraken.model.item.Item
import dev.komu.kraken.model.item.armor.Armoring
import dev.komu.kraken.model.item.food.Corpse
import dev.komu.kraken.model.item.food.Taste
import dev.komu.kraken.model.item.weapon.NaturalWeapon
import dev.komu.kraken.model.item.weapon.Weapon
import dev.komu.kraken.model.item.weapon.WeaponClass
import dev.komu.kraken.model.region.Cell
import dev.komu.kraken.model.region.Region
import dev.komu.kraken.utils.exp.Expression
import dev.komu.kraken.utils.rollDie
import java.lang.Math.max

abstract class Creature(var name: String): MessageTarget {

    var cellOrNull: Cell? = null
        set(cell) {
            field?.creature = null
            field = cell
            field?.creature = this
        }

    var naturalWeapon: Attack = NaturalWeapon("hit", 0, Expression.random(1..3))

    var cell: Cell
        get() = cellOrNull ?: error("no current cell for $this")
        set(cell) { cellOrNull = cell }

    var letter = '\u0000'
        get() = if (field == '\u0000') name[0] else field

    var color = Color.GRAY
    var hitPoints = 1
    abstract val isFriendly: Boolean
    var immobile = false
    var corporeal = true
    var omniscient = false
    var level = 1
    var killExperience = -1
        get() = if (field != -1) field else level * level

    var hitBonus = 0

    var armorClass = 10
        get() = field - armoring.totalArmorBonus

    var luck: Int = 0
    val energy = Energy()
    var baseSpeed = Energy.NORMAL_SPEED
    var weight = 50 * 1000
    var canUseDoors: Boolean = false
    var corpsePoisonousness = Expression.random(1..3)
    var wieldedWeapon: Weapon? = null
    var strength = rollDie(10, 10)
    var charisma = rollDie(10, 10)
    var taste = Taste.CHICKEN
    val armoring = Armoring()
    val inventory = Inventory()

    val speed: Int
        get() = (baseSpeed - weightPenalty).coerceAtLeast(Energy.MIN_SPEED)

    private val weightPenalty: Int
        get() {
            val carriedKilos = weightOfCarriedItems / 1000
            return carriedKilos / (strength * 2)
        }

    val weightOfCarriedItems: Int
        get() = (wieldedWeapon?.weight ?: 0) + armoring.weight + inventory.weight

    open fun getProficiency(weaponClass: WeaponClass): Int = 0

    val isPlayer: Boolean
        get() = this is Player

    val region: Region
        get() = cell.region

    protected val game: Game
        get() = region.world.game

    fun seesCreature(creature: Creature) =
        canSee(creature.cell)

    fun isAdjacentToCreature(creature: Creature) =
        cell.isAdjacent(creature.cell)

    val isAlive: Boolean
        get() = hitPoints > 0 && cellOrNull != null

    abstract fun getAction(game: Game): Action?

    fun moveTowardsAction(targetCell: Cell): Action? {
        val searcher = CreatureShortestPathSearcher(this)
        val first = searcher.findFirstCellOnShortestPath(cell, targetCell)

        return if (first != null)
            MoveAction(this, cell.getDirection(targetCell))
        else
            null
    }

    fun canMoveTo(cell: Cell): Boolean =
        cell.canMoveInto(corporeal)

    open fun canSee(target: Cell): Boolean {
        if (omniscient)
            return true

        return calculateCanSee(target)
    }

    private fun calculateCanSee(target: Cell): Boolean =
        cell.hasLineOfSight(target)

    val adjacentCreatures: Collection<Creature>
        get() = cell.adjacentCells.mapNotNull(Cell::creature)

    override fun toString() = "$name [hp=$hitPoints]"

    open fun onAttackedBy(attacker: Creature) {
    }

    open fun onSuccessfulHit(target: Creature, weapon: Attack) {
    }

    open fun onKilledCreature(target: Creature) {
    }

    open fun takeDamage(points: Int, attacker: Creature, cause: String = attacker.name) {
        hitPoints = max(0, hitPoints - points)

        if (!isAlive) {
            attacker.message("%s %s.", You(), verb("die"))
            if (attacker != this)
                message("%s %s.", You(), verb("die"))
            die(cause)
        }
    }

    open fun talk(target: Creature) {
        target.say(this, "Hrmph.")
    }

    open val attack: Attack
        get() = wieldedWeapon ?: naturalWeapon

    open fun you(): String = name

    @Suppress("FunctionName")
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
        corpse.effectiveness = max((0.05 * weight).toInt(), 800)
        corpse.taste = taste
        return corpse
    }

    open fun die(killer: String) {
        hitPoints = 0
        cell.items.addAll(inventory.items)
        inventory.items.clear()

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

    override fun message(pattern: String, vararg args: Any?) {
    }

    open fun say(talker: Creature, message: String, vararg args: Any?) {
    }

    open fun ask(defaultValue: Boolean, question: String, vararg args: Any?) =
        defaultValue
}
