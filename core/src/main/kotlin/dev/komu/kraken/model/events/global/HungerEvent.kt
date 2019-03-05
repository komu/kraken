package dev.komu.kraken.model.events.global

import dev.komu.kraken.model.Game
import dev.komu.kraken.model.events.PersistentEvent

class HungerEvent: PersistentEvent(100) {
    override fun fire(game: Game) {
        game.player.increaseHungriness(game)
    }
}
