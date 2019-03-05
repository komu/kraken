package dev.komu.kraken.desktop.game.action

import dev.komu.kraken.model.GameFacade
import java.awt.event.ActionEvent
import javax.swing.AbstractAction

abstract class GameAction(name: String, game: GameFacade? = null): AbstractAction(name) {

    var game: GameFacade? = game
        set(game) {
            field = game
            isEnabled = game != null
        }

    init {
        isEnabled = false
    }

    override fun actionPerformed(e: ActionEvent) {
        val g = game
        if (g != null)
            actionPerformed(e, g)
    }

    protected abstract fun actionPerformed(e: ActionEvent, game: GameFacade)
}
