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
