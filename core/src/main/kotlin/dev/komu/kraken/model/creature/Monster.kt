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
