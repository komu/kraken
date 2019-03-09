package dev.komu.kraken.model.creature.pets

import dev.komu.kraken.model.Game
import dev.komu.kraken.model.actions.Action
import dev.komu.kraken.model.creature.Creature

class Lassie(name: String): Pet(name) {

    init {
        weight = 25
        letter = 'C'
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
