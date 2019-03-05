package dev.komu.kraken.model.events

import dev.komu.kraken.model.common.Actor

abstract class GameEvent(var rate: Int): Actor {
    override val destroyed: Boolean
        get() = rate == 0
}
