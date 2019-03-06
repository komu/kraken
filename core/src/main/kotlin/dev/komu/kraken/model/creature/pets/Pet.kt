package dev.komu.kraken.model.creature.pets

import dev.komu.kraken.model.Game
import dev.komu.kraken.model.actions.Action
import dev.komu.kraken.model.actions.AttackAction
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

    override fun getAction(game: Game): Action? {
        val player = game.player

        val enemy = adjacentCreatures.find { !it.isPlayer }
        return when {
            enemy != null ->
                AttackAction(enemy, this)
            seesCreature(player) -> {
                lastKnownPlayerPosition = player.cell
                if (isAdjacentToCreature(player) || Probability.check(50))
                    moveRandomly()
                else
                    moveTowards(player.cell)
            }
            else -> {
                if (cell == lastKnownPlayerPosition)
                    lastKnownPlayerPosition = null

                lastKnownPlayerPosition?.let(this::moveTowards) ?: moveRandomly()
            }
        }
    }

    override val naturalAttack: Attack
        get() = naturalWeapon
}
