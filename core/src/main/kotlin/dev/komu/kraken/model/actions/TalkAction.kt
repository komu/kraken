package dev.komu.kraken.model.actions

import dev.komu.kraken.model.creature.Creature

class TalkAction(private val target: Creature, private val talker: Creature) : Action {
    override fun perform(): ActionResult {
        target.talk(talker)
        return ActionResult.Success
    }
}
