package dev.komu.kraken.model.creature

import dev.komu.kraken.model.region.Cell
import dev.komu.kraken.model.region.ShortestPathSearcher

class CreatureShortestPathSearcher(val creature: Creature): ShortestPathSearcher(creature.region) {

    override fun costToEnter(cell: Cell): Int =
        when {
            cell.creature != null -> 5
            cell.isClosedDoor     -> if (creature.canUseDoors) 2 else 10
            else                  -> 1
        }

    override fun canEnter(cell: Cell) =
        !creature.corporeal || cell.isPassable
}
