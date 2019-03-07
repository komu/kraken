package dev.komu.kraken.model.actions

import dev.komu.kraken.model.Game

class EnterRegionAction(private val game: Game, private val region: String, private val location: String) : Action {

    override fun perform(): ActionResult {
        game.enterRegion(region, location)
        return ActionResult.Success
    }
}
