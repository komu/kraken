package dev.komu.kraken.model.creature

import dev.komu.kraken.model.Game
import dev.komu.kraken.model.actions.Action
import dev.komu.kraken.model.actions.Behavior
import dev.komu.kraken.model.common.Attack
import dev.komu.kraken.model.common.Color
import dev.komu.kraken.model.item.Item
import dev.komu.kraken.model.item.weapon.NaturalWeapon
import dev.komu.kraken.model.item.weapon.WeaponClass
import dev.komu.kraken.model.region.Cell
import dev.komu.kraken.model.region.CellSet
import dev.komu.kraken.model.skill.Proficiency
import dev.komu.kraken.model.skill.SkillSet
import dev.komu.kraken.utils.Expression
import dev.komu.kraken.utils.randomInt
import dev.komu.kraken.utils.rollDie
import kotlin.properties.Delegates

class Player(name: String): Creature(name) {

    var experience = 0
    val skills = SkillSet()
    var visibleCells: CellSet by Delegates.notNull()
    var hunger = 2000
    var fainted = false
    var regenerated = false
    var sight = 20
    var behavior: Behavior? = null

    init {
        letter = '@'
        color = Color.BLUE
        maximumHitPoints = 8 + rollDie(5)
        hitPoints = maximumHitPoints
        canUseDoors = true
        skills.setWeaponProficiency(WeaponClass.SWORD, Proficiency.BASIC)
        naturalWeapon = NaturalWeapon("hit", 0, Expression.random(1..3))
    }

    val activatedItems: List<Item>
        get() {
            val result = mutableListOf<Item>()

            val weapon = wieldedWeapon
            if (weapon != null)
                result.add(weapon)

            result += armoring

            return result
        }

    override fun die(killer: String) {
        if (game.config.wizardMode && !game.ask("Die?")) {
            regenerate()
            game.message("You regenerate.")
        } else {
            game.gameOver(killer)
        }
    }

    private fun regenerate() {
        regenerated = true

        hitPoints = maximumHitPoints
        hunger = 1000
    }

    override fun getProficiency(weaponClass: WeaponClass) =
        skills.getWeaponProficiency(weaponClass).bonus

    override val isFriendly = true

    override fun onSuccessfulHit(target: Creature, weapon: Attack) {
        skills.exerciseSkill(weapon.weaponClass, this)
    }

    override fun onKilledCreature(target: Creature) {
        addExperience(target.killExperience)
    }

    fun decreaseHungriness(effectiveness: Int) {
        hunger += effectiveness
    }

    fun increaseHungriness(game: Game) {
        if (fainted)
            return

        hunger--
        if (hunger < 0) {
            message("You faint.")
            hitPoints = 1
            fainted = true
            game.globalClock.scheduleOnce(randomInt(5, 50)) {
                if (fainted) {
                    message("You wake up.")
                    hunger = hunger.coerceAtLeast(10)

                    fainted = false
                }
            }
        }
    }

    val hungerLevel: HungerLevel
        get() = HungerLevel(hunger)

    private fun addExperience(exp: Int) {
        experience += exp
        if (experience >= experienceNeededForNextLevel)
            gainLevel()
    }

    private fun gainLevel() {
        val newHp = 3 + rollDie(3)
        maximumHitPoints += newHp
        hitPoints = (hitPoints + newHp).coerceAtMost(maximumHitPoints)
        level += 1
    }

    private val experienceNeededForNextLevel: Int
        get() = when {
            level < 10 -> 10 * (1 shl level)
            level < 20 -> 10000 * (1 shl (level - 10))
            else       -> 10000000 * (level - 19)
        }

    fun seesCreatures() =
        visibleCells.any { cell ->
            val creature = cell.creature
            creature != null && creature != this
        }

    fun seesNonFriendlyCreatures() =
        visibleCells.any { cell ->
            val creature = cell.creature
            creature != null && creature != this && !creature.isFriendly
        }

    val needsInput: Boolean
        get() {
            if (behavior?.canPerform(this) != true)
                waitForInput()

            return behavior == null
        }

    fun waitForInput() {
        behavior = null
    }

    override fun getAction(game: Game): Action? = behavior?.getAction(this)

    fun regainHitPoint() {
        hitPoints = (hitPoints + 1).coerceAtMost(maximumHitPoints)
    }

    override fun canSee(target: Cell) =
        target in visibleCells

    fun updateVisiblePoints() {
        visibleCells = cell.getVisibleCells(sight)
    }

    val invisibleCells: CellSet
        get() {
            val cells = region.getCells()
            cells.removeAll(visibleCells)
            return cells
        }

    override fun you() = "you"

    override fun verb(verb: String) = verb

    override fun message(pattern: String, vararg args: Any?) =
        game.message(pattern, *args)

    override fun say(talker: Creature, message: String, vararg args: Any?) =
        game.message("\"$message\"", *args)

    override fun ask(defaultValue: Boolean, question: String, vararg args: Any?) =
        game.ask(question, *args)
}
