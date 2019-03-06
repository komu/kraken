package dev.komu.kraken.model.actions

import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.region.Cell
import dev.komu.kraken.model.region.Door

class CloseDoorAction(private val cell: Cell, private val closer: Creature) : Action {
    override fun perform(): ActionResult {
        val door = cell.state as? Door
        if (door != null && door.isOpen) {
            if (cell.creature != null || !cell.items.isEmpty()) {
                closer.message("Something blocks the door.")
                return ActionResult.Failure
            }

            door.close(closer)
            return ActionResult.Success
        }

        return ActionResult.Failure
    }
}
