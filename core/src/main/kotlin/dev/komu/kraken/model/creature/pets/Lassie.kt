package dev.komu.kraken.model.creature.pets

import dev.komu.kraken.model.Game
import dev.komu.kraken.model.actions.Action
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.creature.Monster
import dev.komu.kraken.model.creature.PetState
import dev.komu.kraken.model.item.weapon.NaturalWeapon
import dev.komu.kraken.utils.exp.Expression

class Lassie(name: String): Monster(name) {

    init {
        weight = 25
        letter = 'C'
        naturalWeapon = NaturalWeapon("bite", 1, Expression.random(3..7))
        state = PetState
    }

    override fun talk(target: Creature) {
        target.message("$name barks.")
    }

    override fun getAction(game: Game): Action? {
        val escape = region.find { it.getJumpTarget(true)?.isExit ?: false }

        return when {
            escape == cell -> {
                hitPoints = 0
                removeFromGame()
                game.message("$name went home.")
                null
            }
            escape != null ->
                moveTowardsAction(escape) ?: super.getAction(game)
            else ->
                super.getAction(game)
        }
    }
}
