/*
 * Copyright 2013 The Wanhack Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.wanhack.model.region

import net.wanhack.model.common.Direction
import net.wanhack.model.creature.Creature
import net.wanhack.model.creature.Player
import net.wanhack.model.item.Item
import net.wanhack.utils.cellsAtDistance
import net.wanhack.utils.signum
import java.util.*
import java.lang.Math.*
import net.wanhack.model.common.Directions
import kotlin.support.AbstractIterator

class Cell(val region: Region, val x: Int, val y: Int, var state: CellState) {

    var hasBeenSeen = false
    val items = HashSet<Item>()
    var creature: Creature? = null
    var portal: Portal? = null
    var defaultLighting = 100
    var lighting = defaultLighting
    var lightPower = 0

    fun enter(creature: Creature) {
        creature.cell = this

        if (state.cellType.isStairs())
            creature.message("You see stairs here.")

        if (items.size == 1)
            creature.message("You see here %s.", items.first().title)
        else if (items.size > 1)
            creature.message("You see multiple items here.")
    }

    fun openDoor(opener: Creature) {
        (state as? Door)?.open(opener)
    }

    fun closeDoor(closer: Creature): Boolean {
        val door = state as? Door
        if (door != null) {
            if (door.isOpen) {
                if (creature != null || !items.isEmpty()) {
                    closer.message("Something blocks the door.")
                    return false
                }

                door.close(closer)
                return true
            }
        }

        return false
    }

    fun getLargestItem(): Item? {
        var maxWeight = Integer.MIN_VALUE
        var largest: Item? = null

        for (item in items)
            if (item.weight > maxWeight) {
                maxWeight = item.weight
                largest = item
            }

        return largest
    }

    fun addItem(item: Item) {
        items.add(item)
    }

    fun addItems(items: Collection<Item>) {
        this.items.addAll(items)
    }

    fun removeItem(item: Item){
        items.remove(item)
    }

    fun isReachable(goal: Cell) =
        this == goal || region.findPath(this, goal) != null

    fun getCellTowards(direction: Direction) =
        region.getCell(x + direction.dx, y + direction.dy)

    fun getJumpTarget(up: Boolean): JumpTarget? =
        portal?.getTarget(up)

    fun isFloor() = state.cellType.isFloor()

    fun isInRoom() = state.cellType.isRoomFloor()

    fun isClosedDoor() = state.cellType == CellType.CLOSED_DOOR

    fun isAdjacent(cell: Cell): Boolean {
        val dx = abs(x - cell.x)
        val dy = abs(y - cell.y)
        return cell != this && dx < 2 && dy < 2
    }

    fun isRoomCorner(): Boolean {
        if (countPassableMainNeighbours() != 2)
            return false

        var previousPassable: Int = 0
        for (d in Direction.values()) {
            val cell = getCellTowards(d)
            if (cell.isPassable()) {
                if (previousPassable == 2) {
                    return true
                } else {
                    previousPassable++
                }
            } else {
                previousPassable = 0
            }
        }
        return false
    }

    fun search(player: Player) =
        state.search(player)

    val cellType: CellType
        get() = state.cellType

    fun setType(cellType: CellType) {
        state = DefaultCellState(cellType)
    }

    fun canDropItemToCell() =
        state.cellType.canDropItem()

    fun canSeeThrough() = state.cellType.canSeeThrough()

    fun isPassable() = state.cellType.isPassable()

    fun canMoveInto(corporeal: Boolean) = creature == null && state.cellType.canMoveInto(corporeal)

    fun distance(cell: Cell): Int {
        val dx = x - cell.x
        val dy = y - cell.y
        return sqrt((dx * dx + dy * dy).toDouble()).toInt()
    }

    fun isInteresting() =!state.cellType.isFloor() || !items.empty

    fun getMatchingCellsNearestFirst(predicate: (Cell) -> Boolean): Iterator<Cell> {
        val maxDistance: Int = getMaximumDistanceFromEdgeOfRegion()

        return object : AbstractIterator<Cell>() {
            private var distance = 0
            private var pos = 0
            private var cellsAtCurrentDistance: List<Cell> = Collections.emptyList()

            protected override fun computeNext() {
                while (pos == cellsAtCurrentDistance.size) {
                    if (distance >= maxDistance) {
                        done()
                        return
                    }

                    cellsAtCurrentDistance = getMatchingCellsAtDistance(++distance, predicate)
                    pos = 0
                }
                setNext(cellsAtCurrentDistance[pos++])
            }
        }
    }

    private fun getMaximumDistanceFromEdgeOfRegion(): Int {
        val maxX = max(x, region.width - x)
        val maxY = max(y, region.height - y)
        return max(maxX, maxY)
    }

    public fun getMatchingCellsAtDistance(distance: Int, predicate: (Cell) -> Boolean): List<Cell> {
        if (distance == 0) {
            if (predicate(this)) {
                return Collections.singletonList(this)
            } else {
                return Collections.emptyList()
            }
        }

        val count = cellsAtDistance(distance)
        val cells = ArrayList<Cell>(count)
        val x1: Int = x - distance
        val y1: Int = y - distance
        val x2: Int = x + distance
        val y2: Int = y + distance
        for (xx in x1..x2) {
            val cell = region.getCellOrNull(xx, y1)
            if (cell != null && predicate(cell))
                cells.add(cell)
        }

        for (yy in y1 + 1..y2 - 1) {
            val left = region.getCellOrNull(x1, yy)
            if (left != null && predicate(left))
                cells.add(left)

            val right = region.getCellOrNull(x2, yy)
            if (right != null && predicate(right))
                cells.add(right)

        }

        for (xx in x1..x2) {
            val cell = region.getCellOrNull(xx, y2)
            if (cell != null && predicate(cell))
                cells.add(cell)
        }

        return cells
    }

    fun getAdjacentCells(): List<Cell> {
        val adjacent = ArrayList<Cell>(8)
        for (d in Direction.values()) {
            val cell = region.getCellOrNull(x + d.dx, y + d.dy)
            if (cell != null)
                adjacent.add(cell)
        }
        return adjacent
    }

    fun getAdjacentCellsOfType(cellType: CellType): List<Cell> {
        val adjacent = ArrayList<Cell>(8)

        for (d in Direction.values()) {
            val cell = region.getCellOrNull(x + d.dx, y + d.dy)
            if (cell != null && cell.cellType == cellType)
                adjacent.add(cell)
        }

        return adjacent
    }

    fun countPassableMainNeighbours(): Int {
        var count = 0
        for (d in Directions.mainDirections)
            if (getCellTowards(d).isPassable())
                count++
        return count
    }

    fun getDirection(cell: Cell): Direction? {
        val dx = signum(cell.x - x)
        val dy = signum(cell.y - y)
        for (d in Direction.values())
            if (dx == d.dx && dy == d.dy)
                return d

        return null
    }

    fun getAdjacentCellsInMainDirections(): List<Cell> {
        val adjacent = ArrayList<Cell>(4)

        for (d in Directions.mainDirections) {
            val cell = region.getCellOrNull(x + d.dx, y + d.dy)
            if (cell != null)
                adjacent.add(cell)
        }

        return adjacent
    }

    fun getCellsBetween(target: Cell): List<Cell> {
        val cells = ArrayList<Cell>(distance(target))
        var x0 = x
        var y0 = y
        var x1 = target.x
        var y1 = target.y

        val steep = abs(y1 - y0) > abs(x1 - x0)
        if (steep) {
            var t0 = x0
            x0 = y0
            y0 = t0
            var t1 = x1
            x1 = y1
            y1 = t1
        }

        val reverse = x0 > x1
        if (reverse) {
            var t0 = x0
            x0 = x1
            x1 = t0
            var t1 = y0
            y0 = y1
            y1 = t1
        }

        val deltax = x1 - x0
        val deltay = abs(y1 - y0)
        var error: Int = 0
        val deltaerr = deltay
        var y: Int = y0
        val ystep = if ((y0 < y1)) 1 else -1

        for (x in x0..x1 - 1) {
            if ((x != x0 || y != y0) && (x != x1 || y != y1))
            {
                if (steep)
                    cells.add(region.getCell(y, x))
                else
                    cells.add(region.getCell(x, y))
            }

            error += deltaerr
            if (2 * error >= deltax) {
                y += ystep
                error -= deltax
            }
        }

        if (reverse)
            Collections.reverse(cells)

        return cells
    }

    fun resetLighting() {
        lighting = defaultLighting
    }

    fun updateLighting(): Unit {
        val lightSourceEffectiveness = calculateLightSourceEffectiveness()
        if (lightSourceEffectiveness > 0) {
            val sight = lightSourceEffectiveness / 10
            val cells = VisibilityChecker().getVisibleCells(this, sight)
            for (cell in cells) {
                val distance = distance(cell)
                val level = lightSourceEffectiveness - 10 * distance
                if (level > 0)
                    cell.lighting += level
            }
        }
    }

    private fun calculateLightSourceEffectiveness(): Int {
        var effectiveness = 0
        for (item in items)
            effectiveness += item.lighting

        effectiveness += creature?.lighting ?: 0

        return lightPower + effectiveness
    }

    fun toString() = "($x, $y)"

    class object {
        public fun getDirectionOfPoint(x1: Int, y1: Int, x2: Int, y2: Int): Direction? {
            val dx = signum(x2 - x1)
            val dy = signum(y2 - y1)

            for (d in Direction.values())
                if (dx == d.dx && dy == d.dy)
                    return d

            return null
        }
    }
}
