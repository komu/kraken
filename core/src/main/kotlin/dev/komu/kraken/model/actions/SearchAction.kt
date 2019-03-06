package dev.komu.kraken.model.actions

import dev.komu.kraken.model.creature.Player

class SearchAction(private val player: Player) : Action {
    override fun perform(): ActionResult {
        for (cell in player.cell.adjacentCells)
            if (cell.state.search(player))
                break

        return ActionResult.Success
    }
}
