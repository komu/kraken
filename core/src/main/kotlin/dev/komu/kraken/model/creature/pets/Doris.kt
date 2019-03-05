package dev.komu.kraken.model.creature.pets

import dev.komu.kraken.model.Game
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.utils.Probability
import dev.komu.kraken.utils.randomItem

class Doris(name: String): Pet(name) {

    init {
        weight = 10
        letter = 'f'
    }

    override fun talk(target: Creature) {
        val verb = randomItem("meows", "purrs")
        target.message("%s %s.", name, verb)
    }

    override fun onTick(game: Game) {
        val player = game.player
        if (isAdjacentToCreature(player) && Probability.check(1))
            game.attack(this, player)
        else
            super.onTick(game)
    }
}
