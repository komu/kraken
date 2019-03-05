package dev.komu.kraken.model.events.global

import dev.komu.kraken.model.Game
import dev.komu.kraken.model.events.PersistentEvent

class RegainHitPointsEvent: PersistentEvent(50 * 100) {
    override fun fire(game: Game) {
        game.player.regainHitPoint()
    }
}
