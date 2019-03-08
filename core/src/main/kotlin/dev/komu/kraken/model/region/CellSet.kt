package dev.komu.kraken.model.region

interface CellSet : Set<Cell> {

    fun copy(): MutableCellSet
    fun randomElement(): Cell
    fun contains(x: Int, y: Int): Boolean

    fun randomElementOrNull(): Cell? =
        if (isEmpty()) null else randomElement()

    fun randomCellMatching(predicate: (Cell) -> Boolean): Cell? {
        var tries = 0
        while (isNotEmpty() && tries++ < 100) {
            val cell = randomElement()
            if (predicate(cell))
                return cell
        }
        return null
    }
}
