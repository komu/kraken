package dev.komu.kraken.model.actions

import dev.komu.kraken.model.Direction
import dev.komu.kraken.model.creature.Creature

class MoveAction(private val creature: Creature, private val towards: Direction) : Action {
    override fun perform(): ActionResult {
        val target = creature.cell.getCellTowards(towards)
        return when {
            creature.canMoveTo(target) -> {
                target.enter(creature)
                ActionResult.Success
            }
            target.isClosedDoor && creature.canUseDoors ->
                ActionResult.Alternate(OpenDoorAction(target, creature))
            else ->
                ActionResult.Failure
        }
    }
}
