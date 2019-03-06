package dev.komu.kraken.model.actions

import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.creature.Player
import dev.komu.kraken.model.item.food.Food

class TalkAction(private val target: Creature, private val talker: Creature) : Action {
    override fun perform(): ActionResult {
        target.talk(talker)
        return ActionResult.Success
    }
}
