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

import java.util.Random
import net.wanhack.model.region.Cell
import net.wanhack.model.region.CellType
import net.wanhack.model.region.Door
import net.wanhack.model.region.Region
import net.wanhack.model.region.World
import net.wanhack.utils.Probability
import org.apache.commons.logging.LogFactory
import net.wanhack.service.region.generators.RoomFirstRegionGenerator.RegionParameters
import net.wanhack.utils.collections.randomElement

class RoomFirstRegionGenerator(val world: World, val name: String, val level: Int, val rp: RegionParameters, val up: String?, val down: String?) {
    private val region = Region(world, name, level, rp.width, rp.height)
    private val connectConnectedProbability = Probability(50)
    private val doorProbability = Probability(30)
    private val hiddenDoorProbability = Probability(15)
    private val overlapTries: Int = 20
    private val random = Random()
    private val log = LogFactory.getLog(javaClass)

    fun generate(): Region {
        val rooms = createRooms()
        createCorridors(rooms)
        createDoors()
        addStairsUpAndDown(rooms)
        return region
    }

    private fun createRooms(): List<Room> {
        val roomCount = rp.minRooms + random.nextInt(rp.maxRooms - rp.minRooms + 1)
        val rooms = listBuilder<Room>()

        roomCount.times {
            rooms.add(createRoom())
        }

        return rooms.build()
    }

    private fun createRoom(): Room {
        var room = randomRoom()
        var tries = overlapTries
        while (overlapsExisting(room) && tries-- > 0) {
            room = randomRoom()
        }
        room.addToRegion()
        return room
    }

    private fun createDoors() {
        for (cell in region) {
            if (isDoorCandidate(cell) && doorProbability.check()) {
                val hidden = hiddenDoorProbability.check()
                cell.state = Door(hidden)
            }
        }
    }

    private fun isDoorCandidate(cell: Cell): Boolean {
        if (cell.cellType != CellType.HALLWAY_FLOOR)
            return false

        var roomNeighbour: Cell? = null
        var hallwayNeighbour: Cell? = null
        var walls: Int = 0
        for (neighbour in cell.adjacentCellsInMainDirections) {
            if (neighbour.cellType == CellType.ROOM_FLOOR)
                roomNeighbour = neighbour
            else if (neighbour.cellType == CellType.HALLWAY_FLOOR)
                hallwayNeighbour = neighbour
            else if (neighbour.cellType  == CellType.ROOM_WALL)
                walls++
        }

        if (roomNeighbour != null && hallwayNeighbour != null && walls == 2) {
            val room = cell.getDirection(roomNeighbour!!)!!
            val hall = cell.getDirection(hallwayNeighbour!!)!!
            if (room.isOpposite(hall))
                return true
        }

        return false
    }

    private fun createCorridors(rooms: List<Room>) {
        var count = 0
        while (!allConnected(rooms)) {
            val room1 = rooms.randomElement()
            val room2 = random(rooms, room1)
            if (!connected(room1, room2) || connectConnected())
                connect(room1, room2)

            if (count > 1000) {
                log.warn("Count exceeded, bailing out.")
                break
            }
            count++
        }
    }

    private fun connectConnected() =
        connectConnectedProbability.check()

    private fun connect(start: Room, goal: Room) {
        val startCell = start.getRandomCell(random)
        val goalCell = goal.getRandomCell(random)
        var previous: Cell? = null
        for (cell in createPath(startCell, goalCell)!!) {
            if (cell.cellType != CellType.ROOM_FLOOR)
                cell.setType(CellType.HALLWAY_FLOOR)

            if (cell !in start)
                for (adjacent in cell.adjacentCellsInMainDirections)
                    if (adjacent != previous && adjacent.isPassable())
                        return

            previous = cell
        }
    }
    private fun createPath(start: Cell, goal: Cell): List<Cell>? =
        CorridorPathSearcher(region).findShortestPath(start, goal)

    private fun random<T>(items: List<T>, invalid: T): T {
        var result = invalid
        while (result == invalid)
            result = items.randomElement()

        return result
    }

    private fun allConnected(rooms: List<Room>) =
        rooms.all { connected(rooms[0], it) }

    private fun connected(room1: Room, room2: Room) =
        room1.getMiddleCell().isReachable(room2.getMiddleCell())

    private fun overlapsExisting(room: Room): Boolean {
        for (yy in 0..room.h - 1) {
            for (xx in 0..room.w - 1) {
                val cell = region.getCell(room.x + xx, room.y + yy)
                if (cell.cellType != CellType.WALL)
                    return true
            }
        }
        return false
    }

    fun addStairsUpAndDown(rooms: List<Room>) {
        val stairsUpRoom = rooms.randomElement()
        val empty = region.getRoomFloorCells()
        if (empty.size < 2)
            throw IllegalStateException("not enough empty cells to place stairs")

        val stairsUp = stairsUpRoom.getRandomCell(random)
        stairsUp.setType(CellType.STAIRS_UP)
        if (up != null)
            region.addPortal(stairsUp.x, stairsUp.y, up, "from down", true)

        region.addStartPoint("from up", stairsUp.x, stairsUp.y)

        if (down != null) {
            while (true)
            {
                val stairsDownRoom = random(rooms, stairsUpRoom)
                val stairsDown = stairsDownRoom.getRandomCell(random)
                if (stairsDown != stairsUp && region.findPath(stairsUp, stairsDown) != null) {
                    stairsDown.setType(CellType.STAIRS_DOWN)
                    region.addPortal(stairsDown.x, stairsDown.y, down, "from up", false)
                    region.addStartPoint("from down", stairsDown.x, stairsDown.y)
                    return
                }
            }
        }
    }

    private fun randomRoom(): Room {
        val w = rp.roomMinWidth + random.nextInt(1 + rp.roomMaxWidth - rp.roomMinWidth)
        val h = rp.roomMinHeight + random.nextInt(1 + rp.roomMaxHeight - rp.roomMinHeight)
        val x = 1 + random.nextInt(region.width - w - 2)
        val y = 1 + random.nextInt(region.height - h - 2)
        return Room(region, x, y, w, h)
    }

    private class Room(val region: Region, val x: Int, val y: Int, val w: Int, val h: Int) {
        fun contains(cell: Cell) = cell.x >= x && cell.x < x + w && cell.y >= y && cell.y < y + h

        fun getRandomCell(random: Random): Cell {
            val xx = x + 1 + random.nextInt(w - 2)
            val yy = y + 1 + random.nextInt(h - 2)
            return region.getCell(xx, yy)
        }

        fun getMiddleCell(): Cell = region.getCell(x + w / 2, y + h / 2)

        fun addToRegion() {
            for (yy in 1..h - 1 - 1) {
                for (xx in 1..w - 1 - 1) {
                    region.getCell(x + xx, y + yy).setType(CellType.ROOM_FLOOR)
                }
            }
            for (xx in 0..w - 1) {
                region.getCell(x + xx, y).setType(CellType.ROOM_WALL)
                region.getCell(x + xx, y + h - 1).setType(CellType.ROOM_WALL)
            }
            for (yy in 0..h - 1) {
                region.getCell(x, y + yy).setType(CellType.ROOM_WALL)
                region.getCell(x + w - 1, y + yy).setType(CellType.ROOM_WALL)
            }
        }
    }

    class object : RegionGenerator {
        override fun generate(world: World, name: String, level: Int, up: String?, down: String?): Region =
            RoomFirstRegionGenerator(world, name, level, regionParameters(level), up, down).generate()

        private fun regionParameters(level: Int): RegionParameters {
            val rp = RegionParameters()
            if (level < 5) {
                rp.width = 80
                rp.height = 25
                rp.minRooms = 4
                rp.maxRooms = 10
                rp.roomMinWidth = 6
                rp.roomMaxWidth = 18
                rp.roomMinHeight = 5
                rp.roomMaxHeight = 9
            } else if (level < 10) {
                rp.width = 120
                rp.height = 30
                rp.minRooms = 6
                rp.maxRooms = 12
                rp.roomMinWidth = 6
                rp.roomMaxWidth = 18
                rp.roomMinHeight = 5
                rp.roomMaxHeight = 9
            } else if (level < 20) {
                rp.width = 160
                rp.height = 40
                rp.minRooms = 8
                rp.maxRooms = 16
                rp.roomMinWidth = 6
                rp.roomMaxWidth = 18
                rp.roomMinHeight = 5
                rp.roomMaxHeight = 9
            } else {
                rp.width = 200
                rp.height = 50
                rp.minRooms = 10
                rp.maxRooms = 25
                rp.roomMinWidth = 6
                rp.roomMaxWidth = 18
                rp.roomMinHeight = 5
                rp.roomMaxHeight = 9
            }
            return rp
        }
    }

    class RegionParameters {
        var width = 0
        var height = 0
        var minRooms = 4
        var maxRooms = 10
        var roomMinWidth = 6
        var roomMaxWidth = 18
        var roomMinHeight = 5
        var roomMaxHeight = 9
    }
}
