package dev.komu.kraken.model.actions

import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.region.Cell

class OpenDoorAction(private val cell: Cell, private val opener: Creature) : Action {
    override fun perform(): ActionResult {
        cell.openDoor(opener)
        return ActionResult.Success
    }
}
