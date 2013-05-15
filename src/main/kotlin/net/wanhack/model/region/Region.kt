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

class Region(val world: World, val name: String, val level: Int, val width: Int, val height: Int): Iterable<Cell> {

    private val cells = Array<Cell>(width * height) { index ->
        val x = index % width
        val y = index / width
        Cell(this, x, y, DefaultCellState(CellType.WALL))
    }

    private val startCells = HashMap<String, Cell>();

    {
        for (x in 0..width - 1) {
            getCell(x, 0).setType(CellType.UNDIGGABLE_WALL)
            getCell(x, height - 1).setType(CellType.UNDIGGABLE_WALL)
        }
        for (y in 0..height - 1) {
            getCell(0, y).setType(CellType.UNDIGGABLE_WALL)
            getCell(width - 1, y).setType(CellType.UNDIGGABLE_WALL)
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

    fun getCreatures(): List<Creature> {
        val creatures = listBuilder<Creature>()
        for (cell in cells) {
            val creature = cell.creature
            if (creature != null)
                creatures.add(creature)
        }
        return creatures.build()
    }

    fun findPath(start: Cell, goal: Cell): List<Cell>? =
        ShortestPathSearcher(this).findShortestPath(start, goal)

    fun getCell(x: Int, y: Int): Cell =
        cells[x + y * width]

    fun getCellOrNull(x: Int, y: Int): Cell? {
        val index = x + y * width
        return if (index >= 0 && index < cells.size)
            cells[index]
        else
            null
    }

    fun containsPoint(x: Int, y: Int) = x >= 0 && x < width && y >= 0 && y < height

    fun updateSeenCells(seen: Set<Cell>) {
        for (cell in cells)
            if (cell in seen)
                cell.hasBeenSeen = true
    }

    fun addPortal(x: Int, y: Int, target: String, location: String, up: Boolean) {
        getCell(x, y).portal = Portal(target, location, up)
    }

    fun addStartPoint(pointName: String, x: Int, y: Int) {
        val old = startCells.put(pointName, getCell(x, y))
        if (old != null)
            throw IllegalStateException("Tried to define start point '$pointName' multiple tiles for region '$name'.")
    }

    fun addCreature(creature: Creature, x: Int, y: Int) {
        creature.cell = getCell(x, y)
    }

    fun addItem(x: Int, y: Int, item: Item) {
        getCell(x, y).items.add(item)
    }

    fun getCells(): CellSet {
        val result = CellSet(this)
        for (cell in cells)
            result.add(cell)
        return result
    }

    fun getCellsForItemsAndCreatures() =
        getMatchingCells { it.isFloor() }

    fun getRoomFloorCells(): CellSet =
        getMatchingCells { it.isInRoom() }

    fun getMatchingCells(predicate: (Cell) -> Boolean): CellSet {
        val result = CellSet(this)
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

    class object {
        val DEFAULT_REGION_WIDTH = 80
        val DEFAULT_REGION_HEIGHT = 25
    }
}
