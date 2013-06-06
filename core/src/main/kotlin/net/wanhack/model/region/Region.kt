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

import net.wanhack.model.creature.Creature
import net.wanhack.model.creature.Player
import net.wanhack.model.item.Item
import java.util.HashMap
import net.wanhack.utils.collections.toOption

class Region(val world: World, val name: String, val level: Int, val width: Int, val height: Int): Iterable<Cell> {

    val size = Size(width, height)

    private val cells = Array<Cell>(width * height) { index ->
        val x = index % width
        val y = index / width
        Cell(this, Coordinate(x, y), DefaultCellState(CellType.WALL))
    }

    private val startCells = HashMap<String, Cell>();

    {
        for (x in 0..width - 1) {
            this[x, 0].setType(CellType.UNDIGGABLE_WALL)
            this[x, height - 1].setType(CellType.UNDIGGABLE_WALL)
        }
        for (y in 0..height - 1) {
            this[0, y].setType(CellType.UNDIGGABLE_WALL)
            this[width - 1, y].setType(CellType.UNDIGGABLE_WALL)
        }
    }

    fun reveal() {
        for (cell in cells)
            cell.hasBeenSeen = true
    }

    override fun iterator() = cells.iterator()

    fun setPlayerLocation(player: Player, location: String) {
        player.cell = startCells[location] ?: throw IllegalStateException("Region '$name' has no start point named '$location'.")
    }

    val creatures: Iterator<Creature>
        get() = cells.iterator().flatMap { it.creature.toOption().iterator() }

    fun findPath(start: Cell, goal: Cell) =
        ShortestPathSearcher(this).findShortestPath(start, goal)

    fun get(c: Coordinate) =
        get(c.x, c.y)

    fun get(x: Int, y: Int): Cell =
        if (containsPoint(x, y))
            cells[x + y * width]
        else
            throw IllegalArgumentException("out of bounds: $x/$width, $y/$height")

    fun containsPoint(x: Int, y: Int) = x >= 0 && x < width && y >= 0 && y < height

    fun updateSeenCells(seen: Set<Cell>) {
        for (cell in seen)
            cell.hasBeenSeen = true
    }

    fun addPortal(c: Coordinate, target: String, location: String, up: Boolean) {
        this[c].portal = Portal(target, location, up)
    }

    fun addStartPoint(c: Coordinate, pointName: String) {
        val old = startCells.put(pointName, this[c])
        if (old != null)
            throw IllegalStateException("Tried to define start point '$pointName' multiple tiles for region '$name'.")
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
        getMatchingCells { it.isInRoom }

    fun getMatchingCells(predicate: (Cell) -> Boolean): MutableCellSet {
        val result = MutableCellSet(this)
        for (cell in cells)
            if (predicate(cell))
                result.add(cell)
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
        for (x in 0..width - 1) {
            if (this[x, 0].cellType != CellType.UNDIGGABLE_WALL)
                return false
            if (this[x, height - 1].cellType != CellType.UNDIGGABLE_WALL)
                return false
        }
        for (y in 0..height - 1) {
            if (this[0, y].cellType != CellType.UNDIGGABLE_WALL)
                return false
            if (this[width - 1, y].cellType != CellType.UNDIGGABLE_WALL)
                return false
        }

        return true
    }

    class object {
        val DEFAULT_REGION_WIDTH = 80
        val DEFAULT_REGION_HEIGHT = 25
    }
}
