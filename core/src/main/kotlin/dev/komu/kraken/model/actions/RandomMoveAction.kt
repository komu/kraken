package dev.komu.kraken.model.actions

import dev.komu.kraken.model.Direction
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.utils.Probability

class RandomMoveAction(private val creature: Creature) : Action {
    override fun perform(): ActionResult =
        if (Probability.check(75))
            ActionResult.Alternate(MoveAction(creature, Direction.randomDirection()))
        else
            ActionResult.Success
}
