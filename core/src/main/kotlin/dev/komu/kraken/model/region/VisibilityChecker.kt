package dev.komu.kraken.model.region

fun Cell.getVisibleCells(sight: Int): CellSet {
    val visible = MutableCellSet(region)

    visible.addAll(cellsNearestFirst(sight).filter { it.hasLineOfSight(this) })

    return visible
}
