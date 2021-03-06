package dev.komu.kraken.model.region

import dev.komu.kraken.model.Direction
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.item.Item
import matchAllBetween
import java.util.*
import kotlin.math.max
import kotlin.math.min

class Cell(val region: Region, val coordinate: Coordinate, var state: CellState) {

    var seen = false
    val items = HashSet<Item>()
    var creature: Creature? = null
    var portal: Portal? = null
    private var defaultLighting = 100
    var lighting = defaultLighting
    var lightPower = 0

    fun enter(creature: Creature) {
        creature.cell = this

        if (type.isStairs)
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

    var type: CellType
        get() = state.cellType
        set(type) {
            state = DefaultCellState(type)
        }

    val isFloor: Boolean
        get() = type.isFloor

    val isClosedDoor: Boolean
        get() = type == CellType.CLOSED_DOOR

    val canDropItemToCell: Boolean
        get() = type.canDropItem

    val isPassable: Boolean
        get() = type.passable

    val isInteresting: Boolean
        get() = !type.isFloor || !items.isEmpty()

    val isDeadEnd: Boolean
        get() = isPassable && countPassableMainNeighbours() == 1

    fun canMoveInto(corporeal: Boolean = true) = creature == null && type.canMoveInto(corporeal)

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

    fun hasLineOfSight(target: Cell): Boolean = matchAllBetween(coordinate, target.coordinate) { x, y ->
        val cell = region[x, y]
        cell == target || cell == this || cell.type.canSeeThrough
    }

    fun resetLighting() {
        lighting = defaultLighting
    }

    fun updateLighting() {
        val totalLighting = lightPower + (creature?.inventory?.lighting ?: 0) + items.sumBy { it.lighting }
        if (totalLighting > 0)
            for (cell in getVisibleCells(totalLighting / 10))
                cell.lighting += max(totalLighting - 10 * distance(cell), 0)
    }

    override fun toString() = coordinate.toString()

    fun reveal() {
        seen = true
        state.reveal()
    }
}
