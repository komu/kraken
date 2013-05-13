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

package net.wanhack.service.region.generators

import java.awt.Dimension
import java.util.ArrayList
import java.util.Collections
import java.util.Random
import net.wanhack.model.common.Direction
import net.wanhack.model.region.Cell
import net.wanhack.model.region.CellSet
import net.wanhack.model.region.CellType
import net.wanhack.model.region.Region
import net.wanhack.model.region.World
import net.wanhack.utils.Probability
import net.wanhack.model.common.Directions

open class MazeRegionGenerator: RegionGenerator {
    private var region: Region? = null
    private var width: Int = 0
    private var height: Int = 0
    private val randomness = Probability(40)
    private val sparseness = 5
    private val deadEndsRemoved = Probability(90)
    private val minRooms = 2
    private val maxRooms = 8
    private val roomMinWidth = 3
    private val roomMaxWidth = 10
    private val roomMinHeight = 3
    private val roomMaxHeight = 7
    private val random = Random()
    private val directions = ArrayList(Directions.mainDirections)

    override fun generate(world: World, name: String, level: Int, up: String?, down: String?): Region {
        val region = Region(world, name, level, 80, 25)

        this.width = region.width
        this.height = region.height
        this.region = region
        generateMaze()
        sparsify()
        addLoops()
        addRooms()
        addStairsUpAndDown(up, down)
        return region
    }

    private fun addStairsUpAndDown(upRegion: String?, downRegion: String?): Unit {
        val empty = region!!.getRoomFloorCells()
        if (empty.size < 2)
            throw IllegalStateException("not enough empty cells to place stairs")

        val stairsUp = empty[random.nextInt(empty.size)]
        stairsUp.setType(CellType.STAIRS_UP)

        if (upRegion != null)
            region!!.addPortal(stairsUp.x, stairsUp.y, upRegion, "from down", true)

        region!!.addStartPoint("from up", stairsUp.x, stairsUp.y)

        if (downRegion != null) {
            while (true) {
                val stairsDown = empty[random.nextInt(empty.size)]
                if (stairsDown != stairsUp && region?.findPath(stairsUp, stairsDown) != null) {
                    stairsDown.setType(CellType.STAIRS_DOWN)
                    region?.addPortal(stairsDown.x, stairsDown.y, downRegion, "from up", false)
                    region?.addStartPoint("from down", stairsDown.x, stairsDown.y)
                    return
                }
            }
        }
    }

    private fun generateMaze() {
        val randomX = 1 + random.nextInt(width - 2)
        val randomY = 1 + random.nextInt(height - 2)
        var current = region?.getCell(randomX, randomY)
        current?.setType(CellType.HALLWAY_FLOOR)

        val candidates = CellSet(region!!)
        while (current != null) {
            current = generatePathFrom(current!!, candidates, null, 3, false)
            if (current == null)
                current = randomCandidate(candidates)
        }
    }

    private fun generatePathFrom(current: Cell, candidates: CellSet?, visited: CellSet?, gridSize: Int, stopOnEmpty: Boolean): Cell? {
        val currentX = current.x
        val currentY = current.y

        for (dir in getDirections()) {
            val xx = currentX + gridSize * dir.dx
            val yy = currentY + gridSize * dir.dy
            if (isOk(xx, yy) && (visited == null || !visited.contains(xx, yy))) {
                val cell = region!!.getCell(xx, yy)
                if (!cell.isPassable() && cell.cellType != CellType.UNDIGGABLE_WALL) {
                    for (i in 1..gridSize - 1) {
                        val xxx = currentX + i * dir.dx
                        val yyy = currentY + i * dir.dy
                        region?.getCell(xxx, yyy)?.setType(CellType.HALLWAY_FLOOR)
                    }
                    cell.setType(CellType.HALLWAY_FLOOR)

                    candidates?.add(cell)

                    return cell
                } else if (stopOnEmpty) {
                    for (i in 1..gridSize - 1) {
                        val xxx = currentX + i * dir.dx
                        val yyy = currentY + i * dir.dy
                        region?.getCell(xxx, yyy)?.setType(CellType.HALLWAY_FLOOR)
                    }
                    return null
                }
            }
        }

        candidates?.remove(current)

        return null
    }

    private fun getDirections(): List<Direction> {
        if (randomness.check())
            Collections.shuffle(directions, random)

        return directions
    }

    private open fun sparsify() {
        for (i in 0..sparseness - 1)
            shortenDeadEnds()
    }

    private fun shortenDeadEnds() {
        val removed = CellSet(region!!)
        for (cell in region!!)
            if (isDeadEnd(cell))
                removed.add(cell)

        for (cell in removed)
            cell.setType(CellType.WALL)
    }

    private fun addLoops() {
        for (cell in region!!)
            if (isDeadEnd(cell) && deadEndsRemoved.check())
                removeDeadEnd(cell)
    }

    private fun removeDeadEnd(start: Cell) {
        val visited = CellSet(region!!)

        var current: Cell? = start
        while (current != null) {
            visited.add(current!!)
            current = generatePathFrom(current!!, null, visited, 3, true)
        }
    }

    private fun addRooms() {
        val rooms = minRooms + random.nextInt(1 + maxRooms - minRooms)
        for (i in 0..rooms - 1) {
            addRoom()
        }
    }

    private fun addRoom() {
        val room = randomRoomDimensions()
        val x = 2 + random.nextInt(width - (room.width) - 4)
        val y = 2 + random.nextInt(height - (room.height) - 4)
        createRoom(x, y, room)
    }

    private fun createRoom(x: Int, y: Int, dims: Dimension) {
        for (yy in 0..dims.height)
            for (xx in 0..dims.width)
                region!!.getCell(x + xx, y + yy).setType(CellType.ROOM_FLOOR)
    }

    private fun randomRoomDimensions(): Dimension {
        val w = roomMinWidth + random.nextInt(1 + roomMaxWidth - roomMinWidth)
        val h = roomMinHeight + random.nextInt(1 + roomMaxHeight - roomMinHeight)
        return Dimension(w, h)
    }

    private fun randomCandidate(candidates: CellSet): Cell? {
        if (candidates.empty)
            return null
        else
            return candidates[random.nextInt(candidates.size)]
    }

    private fun isOk(x: Int, y: Int): Boolean {
        return x > 1 && x < width - 1 && y > 1 && y < height - 1
    }

    class object {
        private fun isDeadEnd(cell: Cell) =
            cell.isPassable() && cell.countPassableMainNeighbours() == 1
    }
}
