package dev.komu.kraken.model.region.generators

import dev.komu.kraken.model.region.Cell
import dev.komu.kraken.model.region.CellType.*
import dev.komu.kraken.model.region.Region
import dev.komu.kraken.model.region.ShortestPathSearcher

class CorridorPathSearcher(region: Region): ShortestPathSearcher(region) {

    override val allowSubDirections = false

    override fun canEnter(cell: Cell) = cell.cellType != UNDIGGABLE_WALL

    override fun costToEnter(cell: Cell): Int =
        when (cell.cellType) {
            UNDIGGABLE_WALL -> 100000
            ROOM_FLOOR      -> 100
            HALLWAY_FLOOR   -> 50
            ROOM_WALL       -> 200
            WALL            -> 100
            else            -> 500
        }
}
