package dev.komu.kraken.model.creature.pets

import dev.komu.kraken.model.Game
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.region.Cell

class Lassie(name: String): Pet(name) {

    init {
        weight = 25
        letter = 'C'
    }

    override fun talk(target: Creature) {
        target.message("$name barks.")
    }

    override fun onTick(game: Game) {
        val escape = findEscapeStairs()
        if (escape != null) {
            if (escape == cell) {
                hitPoints = 0
                removeFromGame()
                game.message("$name went home.")
            } else {
                val ok = moveTowards(escape)
                if (!ok)
                    super.onTick(game)
            }
        } else {
            super.onTick(game)
        }
    }

    private fun findEscapeStairs(): Cell? =
        region.find { cell ->
            val target = cell.getJumpTarget(true)
            target != null && target.isExit
        }
}
