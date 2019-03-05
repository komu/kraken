package dev.komu.kraken.model

import dev.komu.kraken.model.item.ItemInfo
import dev.komu.kraken.model.region.CellSet
import dev.komu.kraken.model.region.Coordinate
import dev.komu.kraken.model.region.Region

interface ReadOnlyGame {
    val inventoryItems: List<ItemInfo>
    val statistics: GameStatistics

    val visibleCells: CellSet
    val cellInFocus: Coordinate
    val currentRegionOrNull: Region?
    val selectedCell: Coordinate?
}
