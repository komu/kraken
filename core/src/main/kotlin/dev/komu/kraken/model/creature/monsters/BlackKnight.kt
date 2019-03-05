package dev.komu.kraken.model.creature.monsters

import dev.komu.kraken.definitions.Weapons
import dev.komu.kraken.model.Game
import dev.komu.kraken.model.common.Color
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.creature.Monster
import dev.komu.kraken.model.item.Item
import dev.komu.kraken.model.item.weapon.NaturalWeapon
import dev.komu.kraken.model.region.Cell
import dev.komu.kraken.utils.randomElement
import dev.komu.kraken.utils.shuffled

class BlackKnight : Monster("The Black Knight") {

    private var lastKnownPlayerPosition: Cell? = null
    private val bite = NaturalWeapon("bite", "1", "randint(0, 1)")
    private var hasBeenFighting = false
    private var maxHitPoints = 1

    override var hitPoints: Int = 5
        set(hitPoints) {
            super.hitPoints = hitPoints
            maxHitPoints = Math.max(maxHitPoints, hitPoints)
        }

    init {
        level = 30
        letter = 'p'
        color = Color.BLACK

        hitPoints = 600
        hitBonus = 20
        weight = 80000
        luck = 2
        canUseDoors = true
        killExperience = 4000
        armorClass = -6
        tickRate = 60
        wieldedWeapon = Weapons.blackSword.create()
    }

    override fun talk(target: Creature) {
        val yells = when (hitPointPercentage) {
            in 0..20 -> TORSO_YELLS
            in 21..40 -> ONE_LEGGED_YELLS
            in 41..60 -> ARMLESS_YELLS
            in 61..80 -> ONE_ARMED_YELLS
            else -> HEALTHY_YELLS
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
                wieldedWeapon = null
                dropToAdjacentCell(weapon)
            }
        } else {
            attacker.message("The Black Knight loses a limb.")
        }
    }

    private fun dropToAdjacentCell(item: Item) {
        val target = cell.adjacentCells.shuffled().find(Cell::canDropItemToCell) ?: cell
        target.items.add(item)
    }

    companion object {
        private val HEALTHY_YELLS = listOf("None shall pass.", "I move for no man.", "Aaaagh!")
        private val ONE_ARMED_YELLS = listOf("Tis but a scratch.", "I've had worse.", "Come on, you pansy!")
        private val ARMLESS_YELLS = listOf("Come on, then.", "Have at you!")
        private val ONE_LEGGED_YELLS = listOf("Right. I'll do you for that!", "I'm invincible!")
        private val TORSO_YELLS = listOf("Oh. Oh, I see. Running away, eh?", "You yellow bastard!", "Come back here and take what's coming to you.", "I'll bite your legs off!")
        private val PLAYER_FLEEING_YELLS = listOf("Oh, had enough, eh?", "Just a flesh wound.", "Chicken! Chickennn!")
    }
}
