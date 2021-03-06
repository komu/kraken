package dev.komu.kraken.model.region

import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.creature.Player
import dev.komu.kraken.model.item.Item
import java.util.*

class Region(val world: World, val name: String, val level: Int, val width: Int, val height: Int): Iterable<Cell> {

    val size = Size(width, height)

    private val cells = Array(width * height) { index ->
        val x = index % width
        val y = index / width
        Cell(this, Coordinate(x, y), DefaultCellState(CellType.WALL))
    }

    private val startCells = HashMap<String, Cell>()

    init {
        for (x in 0 until width) {
            this[x, 0].type = CellType.UNDIGGABLE_WALL
            this[x, height - 1].type = CellType.UNDIGGABLE_WALL
        }
        for (y in 0 until height) {
            this[0, y].type = CellType.UNDIGGABLE_WALL
            this[width - 1, y].type = CellType.UNDIGGABLE_WALL
        }
    }

    fun reveal() {
        for (cell in cells)
            cell.reveal()
    }

    override fun iterator() = cells.iterator()

    fun setPlayerLocation(player: Player, location: String) {
        player.cell = startCells[location] ?: error("Region '$name' has no start point named '$location'.")
    }

    val creatures: Sequence<Creature>
        get() = cells.asSequence().mapNotNull(Cell::creature)

    fun findPath(start: Cell, goal: Cell): Iterable<Cell>? =
        ShortestPathSearcher(this).findShortestPath(start, goal)

    operator fun get(c: Coordinate) =
        get(c.x, c.y)

    operator fun get(x: Int, y: Int): Cell {
        require(containsPoint(x, y)) { "out of bounds: $x/$width, $y/$height" }
        return cells[x + y * width]
    }

    fun containsPoint(x: Int, y: Int) = x in 0..(width - 1) && y >= 0 && y < height

    fun updateSeenCells(seen: Set<Cell>) {
        for (cell in seen)
            cell.seen = true
    }

    fun addPortal(c: Coordinate, target: String, location: String, up: Boolean) {
        this[c].portal = Portal(target, location, up)
    }

    fun addStartPoint(c: Coordinate, pointName: String) {
        val old = startCells.put(pointName, this[c])
        if (old != null)
            error("Tried to define start point '$pointName' multiple tiles for region '$name'.")
    }

    fun addCreature(c: Coordinate, creature: Creature) {
        creature.cell = this[c]
    }

    fun addItem(c: Coordinate, item: Item) {
        this[c].items.add(item)
    }

    fun getCells(): MutableCellSet {
        val result = MutableCellSet(this)
        for (cell in cells)
            result.add(cell)
        return result
    }

    fun getCellsForItemsAndCreatures() =
        getMatchingCells { it.isFloor }

    fun getRoomFloorCells(): CellSet =
        getMatchingCells { it.type.isRoomFloor }

    fun getMatchingCells(predicate: (Cell) -> Boolean): MutableCellSet {
        val result = MutableCellSet(this)
        cells.filterTo(result, predicate)
        return result
    }

    fun updateLighting() {
        for (cell in cells)
            cell.resetLighting()

        for (cell in cells)
            cell.updateLighting()
    }

    // This should always be true, but is here for assertions
    fun isSurroundedByUndiggableWalls(): Boolean {
        for (x in 0 until width) {
            if (this[x, 0].type != CellType.UNDIGGABLE_WALL)
                return false
            if (this[x, height - 1].type != CellType.UNDIGGABLE_WALL)
                return false
        }
        for (y in 0 until height) {
            if (this[0, y].type != CellType.UNDIGGABLE_WALL)
                return false
            if (this[width - 1, y].type != CellType.UNDIGGABLE_WALL)
                return false
        }

        return true
    }

    companion object {
        const val DEFAULT_REGION_WIDTH = 80
        const val DEFAULT_REGION_HEIGHT = 25
    }
}
