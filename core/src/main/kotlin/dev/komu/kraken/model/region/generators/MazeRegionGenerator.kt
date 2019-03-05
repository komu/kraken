/*
 * Copyright 2013 The Releasers of Kraken
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

package dev.komu.kraken.model.region.generators

import dev.komu.kraken.model.region.*
import dev.komu.kraken.utils.Probability
import dev.komu.kraken.utils.logger
import dev.komu.kraken.utils.randomInt
import dev.komu.kraken.utils.shuffled

class MazeRegionGenerator(world: World, val name: String, val level: Int, val up: String?, val down: String?) {
    private val region = Region(world, name, level, 80, 25)
    private val randomness = Probability(40)
    private val sparseness = 5
    private val deadEndsRemoved = Probability(90)
    private val minRooms = 2
    private val maxRooms = 8
    private val roomMinWidth = 3
    private val roomMaxWidth = 10
    private val roomMinHeight = 3
    private val roomMaxHeight = 7
    private val log = javaClass.logger()

    fun generate(): Region {
        generateMaze()
        sparsify()
        addLoops()
        addRooms()
        addStairsUpAndDown()
        return region
    }

    private fun addStairsUpAndDown() {
        val empty = region.getRoomFloorCells()
        if (empty.size < 2)
            throw IllegalStateException("not enough empty cells to place stairs")

        val stairsUp = empty.randomElement()
        stairsUp.setType(CellType.STAIRS_UP)

        if (up != null) {
            region.addPortal(stairsUp.coordinate, up, "from down", true)
        }

        region.addStartPoint(stairsUp.coordinate, "from up")

        if (down != null) {
            val stairsDown = empty.findCellConnectedTo(stairsUp)
            if (stairsDown != null) {
                stairsDown.setType(CellType.STAIRS_DOWN)
                region.addPortal(stairsDown.coordinate, down, "from up", false)
                region.addStartPoint(stairsDown.coordinate, "from down")
                return
            } else {
                log.warning("failed to find cell for down-stairs")
            }
        }
    }

    private fun generateMaze() {
        val first = region[randomInt(1, region.width-2), randomInt(1, region.height-2)]
        first.setType(CellType.HALLWAY_FLOOR)

        val candidates = MutableCellSet(region)
        var current = first
        while (true) {
            current = generatePathFrom(current, candidates, null, 3, false) ?: candidates.randomElementOrNull() ?: break
        }
    }

    private fun generatePathFrom(current: Cell, candidates: MutableCellSet?, visited: MutableCellSet?, gridSize: Int, stopOnEmpty: Boolean): Cell? {
        val currentCoordinate = current.coordinate

        for (dir in pathDirections()) {
            val xx = currentCoordinate.x + gridSize * dir.dx
            val yy = currentCoordinate.y + gridSize * dir.dy

            if (isCandidateForPath(xx, yy) && (visited == null || !visited.contains(xx, yy))) {
                val cell = region[xx, yy]
                if (!cell.isPassable && cell.cellType != CellType.UNDIGGABLE_WALL) {
                    for (i in 1..gridSize - 1)
                        region[currentCoordinate.x + i * dir.dx, currentCoordinate.y + i * dir.dy].setType(CellType.HALLWAY_FLOOR)

                    cell.setType(CellType.HALLWAY_FLOOR)

                    candidates?.add(cell)

                    return cell
                } else if (stopOnEmpty) {
                    for (i in 1..gridSize - 1)
                        region[currentCoordinate.x + i * dir.dx, currentCoordinate.y + i * dir.dy].setType(CellType.HALLWAY_FLOOR)

                    return null
                }
            }
        }

        candidates?.remove(current)

        return null
    }

    private fun pathDirections(): List<dev.komu.kraken.common.Direction> =
        if (randomness.check())
            dev.komu.kraken.common.Directions.mainDirections.shuffled()
        else
            dev.komu.kraken.common.Directions.mainDirections

    private fun sparsify() {
        repeat(sparseness) {
            val deadEnds = region.getMatchingCells(Cell::isDeadEnd)
            for (cell in deadEnds)
                cell.setType(CellType.WALL)
        }
    }

    private fun addLoops() {
        for (cell in region)
            if (cell.isDeadEnd && deadEndsRemoved.check())
                removeDeadEnd(cell)
    }

    private fun removeDeadEnd(start: Cell) {
        val visited = MutableCellSet(region)

        var current = start
        while (true) {
            visited.add(current)
            current = generatePathFrom(current, null, visited, 3, true) ?: break
        }
    }

    private fun addRooms() {
        repeat(randomInt(minRooms, maxRooms)) {
            addRoom()
        }
    }

    private fun addRoom() {
        val width = randomInt(roomMinWidth, roomMaxWidth)
        val height = randomInt(roomMinHeight, roomMaxHeight)
        val x = 2 + randomInt(region.width - width - 4)
        val y = 2 + randomInt(region.height - height - 4)

        for (yy in y..y+height)
            for (xx in x..x+width)
                region[xx, yy].setType(CellType.ROOM_FLOOR)
    }

    private fun CellSet.findCellConnectedTo(start: Cell): Cell? {
        var tries = 0
        while (tries++ < 1000) {
            val cell = this.randomElement()
            if (cell != start && region.findPath(start, cell) != null)
                return cell
        }

        return null
    }

    private fun isCandidateForPath(x: Int, y: Int) =
        x > 1 && x < region.width - 1 && y > 1 && y < region.height - 1

    companion object : RegionGenerator {

        override fun generate(world: World, name: String, level: Int, up: String?, down: String?): Region =
            MazeRegionGenerator(world, name, level, up, down).generate()
    }
}
