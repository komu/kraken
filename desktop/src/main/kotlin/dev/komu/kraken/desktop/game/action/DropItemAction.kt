package dev.komu.kraken.desktop.game.action

import dev.komu.kraken.model.GameFacade
import dev.komu.kraken.model.item.Item
import java.awt.event.ActionEvent

class DropItemAction(game: GameFacade?, val item: Item): GameAction("Drop", game) {

    override fun actionPerformed(e: ActionEvent, game: GameFacade) {
        game.drop(item)
    }
}
