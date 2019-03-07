package dev.komu.kraken.model.region

import dev.komu.kraken.common.Direction
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.item.Item
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Cell(val region: Region, val coordinate: Coordinate, var state: CellState) {

    var hasBeenSeen = false
    val items = HashSet<Item>()
    var creature: Creature? = null
    var portal: Portal? = null
    var defaultLighting = 100
    var lighting = defaultLighting
    var lightPower = 0

    fun enter(creature: Creature) {
        creature.cell = this

        if (state.cellType.isStairs)
            creature.message("You see stairs here.")

        if (items.size == 1)
            creature.message("You see here %s.", items.first().title)
        else if (items.size > 1)
            creature.message("You see multiple items here.")
    }

    fun openDoor(opener: Creature) {
        (state as? Door)?.open(opener)
    }

    val largestItem: Item?
        get() = items.maxBy(Item::weight)

    fun isReachable(goal: Cell) = this == goal || region.findPath(this, goal) != null

    fun getCellTowards(direction: Direction) =
        region[coordinate.x + direction.dx, coordinate.y + direction.dy]

    fun getJumpTarget(up: Boolean) = portal?.getTarget(up)

    fun isAdjacent(cell: Cell) = coordinate.isAdjacent(cell.coordinate)

    val isInCorridor: Boolean
        get() = countPassableMainNeighbours() == 2 && !isRoomCorner()

    private fun isRoomCorner(): Boolean {
        if (countPassableMainNeighbours() != 2)
            return false

        var previousPassable = 0

        for (cell in adjacentCells) {
            if (cell.isPassable) {
                if (previousPassable == 2)
                    return true
                else
                    previousPassable++
            } else {
                previousPassable = 0
            }
        }

        return false
    }

    val cellType: CellType
        get() = state.cellType

    fun setType(cellType: CellType) {
        state = DefaultCellState(cellType)
    }

    val isFloor: Boolean
        get() = state.cellType.isFloor

    val isInRoom: Boolean
        get() = state.cellType.isRoomFloor

    val isClosedDoor: Boolean
        get() = state.cellType == CellType.CLOSED_DOOR

    val canDropItemToCell: Boolean
        get() = state.cellType.canDropItem

    val canSeeThrough: Boolean
        get() = state.cellType.canSeeThrough

    val isPassable: Boolean
        get() = state.cellType.passable

    val isInteresting: Boolean
        get() = !state.cellType.isFloor || !items.isEmpty()

    val isDeadEnd: Boolean
        get() = isPassable && countPassableMainNeighbours() == 1

    fun canMoveInto(corporeal: Boolean) = creature == null && state.cellType.canMoveInto(corporeal)

    fun distance(cell: Cell) = coordinate.distance(cell.coordinate)

    fun cellsNearestFirst(): Sequence<Cell> =
        cellsNearestFirst(max(max(coordinate.x, region.width - coordinate.x), max(coordinate.y, region.height - coordinate.y)))

    private fun cellsNearestFirst(maxDistance: Int): Sequence<Cell> = sequence {
        for (distance in 1..maxDistance) {
            val x1 = max(0, coordinate.x - distance)
            val y1 = max(0, coordinate.y - distance)
            val x2 = min(region.width - 1, coordinate.x + distance)
            val y2 = min(region.height - 1, coordinate.y + distance)

            for (xx in x1..x2)
                yield(region[xx, y1])

            for (yy in y1 + 1 until y2) {
                yield(region[x1, yy])
                yield(region[x2, yy])
            }

            for (xx in x1..x2)
                yield(region[xx, y2])
        }
    }

    val adjacentCells: List<Cell>
        get() = adjacentCells(Direction.directions)

    val adjacentCellsInMainDirections: List<Cell>
        get() = adjacentCells(Direction.mainDirections)

    fun getVisibleCells(sight: Int): CellSet {
        val visible = MutableCellSet(region)

        cellsNearestFirst(sight).filterTo(visible) { it.hasLineOfSight(this) }

        return visible
    }

    private fun adjacentCells(directions: Collection<Direction>): List<Cell> {
        val adjacent = ArrayList<Cell>(directions.size)
        for (d in directions) {
            val xx = coordinate.x + d.dx
            val yy = coordinate.y + d.dy
            if (region.containsPoint(xx, yy))
                adjacent.add(region[xx, yy])
        }
        return adjacent
    }

    fun countPassableMainNeighbours() =
        Direction.mainDirections.count { getCellTowards(it).isPassable }

    fun getDirection(cell: Cell) = coordinate.directionOf(cell.coordinate)

    fun hasLineOfSight(target: Cell) =
        cellsBetween(target).all { it == target || it == this || it.canSeeThrough }

    private fun cellsBetween(target: Cell): List<Cell> {
        // see http://en.wikipedia.org/wiki/Bresenham's_line_algorithm
        val cells = ArrayList<Cell>(distance(target))

        var x0 = coordinate.x
        var y0 = coordinate.y
        val x1 = target.coordinate.x
        val y1 = target.coordinate.y

        val dx = abs(x1 - x0)
        val dy = abs(y1 - y0)

        val sx = if (x0 < x1) 1 else -1
        val sy = if (y0 < y1) 1 else -1
        var err = dx - dy

        while (true) {
            cells.add(region[x0, y0])

            if (x0 == x1 && y0 == y1)
                break

            val e2 = 2 * err
            if (e2 > -dy) {
                err -= dy
                x0 += sx
            }
            if (x0 == x1 && y0 == y1) {
                cells.add(region[x0, y0])
                break
            }
            if (e2 < dx) {
                err += dx
                y0 += sy
            }
        }

        return cells
    }

    fun resetLighting() {
        lighting = defaultLighting
    }

    fun updateLighting() {
        val totalLighting = lightPower + (creature?.lighting ?: 0) + items.sumBy { it.lighting }
        if (totalLighting > 0)
            for (cell in getVisibleCells(totalLighting / 10))
                cell.lighting += max(totalLighting - 10 * distance(cell), 0)
    }

    override fun toString() = coordinate.toString()
}
