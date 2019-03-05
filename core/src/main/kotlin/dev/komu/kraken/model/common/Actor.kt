package dev.komu.kraken.model.common

import dev.komu.kraken.model.Game

interface Actor {
    fun act(game: Game): Int
    val destroyed: Boolean
}
