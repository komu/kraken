package dev.komu.kraken.model.region

import dev.komu.kraken.model.creature.Player

class DefaultCellState(override val cellType: CellType): CellState {
    override fun search(searcher: Player) = false
}
