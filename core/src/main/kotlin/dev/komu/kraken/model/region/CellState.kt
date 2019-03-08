package dev.komu.kraken.model.region

import dev.komu.kraken.model.creature.Player

interface CellState {
    val cellType: CellType
    fun search(searcher: Player): Boolean = false
    fun reveal() { }
}
