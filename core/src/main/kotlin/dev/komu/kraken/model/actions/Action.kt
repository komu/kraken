package dev.komu.kraken.model.actions

// inspired by http://journal.stuffwithstuff.com/2014/07/15/a-turn-based-game-loop/
interface Action {
    fun perform(): ActionResult
}

sealed class ActionResult {
    object Success : ActionResult()
    object Failure : ActionResult()
    class Alternate(val action: Action) : ActionResult()
}
