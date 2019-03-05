package dev.komu.kraken.model.region

interface CellSet : Set<Cell> {

    fun copy(): MutableCellSet
    fun randomElement(): Cell
    fun contains(x: Int, y: Int): Boolean

    fun randomElementOrNull(): Cell? =
        if (isEmpty()) null else randomElement()
}
