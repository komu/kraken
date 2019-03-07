package dev.komu.kraken.model.actions

object SkipAction : Action {
    override fun perform(): ActionResult = ActionResult.Success
}
