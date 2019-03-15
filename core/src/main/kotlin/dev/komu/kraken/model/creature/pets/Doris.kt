package dev.komu.kraken.model.creature.pets

import dev.komu.kraken.model.Game
import dev.komu.kraken.model.actions.Action
import dev.komu.kraken.model.actions.AttackAction
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.creature.Monster
import dev.komu.kraken.model.creature.PetState
import dev.komu.kraken.model.item.weapon.NaturalWeapon
import dev.komu.kraken.utils.Probability
import dev.komu.kraken.utils.exp.Expression
import dev.komu.kraken.utils.randomItem

class Doris(name: String): Monster(name) {

    init {
        weight = 10
        letter = 'f'
        naturalWeapon = NaturalWeapon("bite", 1, Expression.random(3..7))
        state = PetState
    }

    override fun talk(target: Creature) {
        val verb = randomItem("meows", "purrs")
        target.message("%s %s.", name, verb)
    }

    override fun getAction(game: Game): Action? {
        val player = game.player
        return if (isAdjacentToCreature(player) && Probability.check(percentage = 1))
            AttackAction(player, this)
        else
            super.getAction(game)
    }
}
