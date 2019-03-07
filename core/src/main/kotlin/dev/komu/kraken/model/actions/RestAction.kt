package dev.komu.kraken.model.actions

import dev.komu.kraken.model.creature.Player

class RestAction(private val player: Player) : Action {
    override fun perform(): ActionResult {
        player.message("Resting...")
        return ActionResult.Success
    }
}
