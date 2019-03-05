package dev.komu.kraken.model.events

import dev.komu.kraken.model.Game

abstract class PersistentEvent(rate: Int): GameEvent(rate) {

    override fun act(game: Game): Int {
        fire(game)
        return rate
    }

    protected abstract fun fire(game: Game)
}
