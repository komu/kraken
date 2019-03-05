package dev.komu.kraken.model.events

import dev.komu.kraken.model.Game

class OneTimeEvent(rate: Int, val callback: (Game) -> Unit): GameEvent(rate) {

    override fun act(game: Game): Int {
        callback(game)
        rate = 0
        return 0
    }
}
