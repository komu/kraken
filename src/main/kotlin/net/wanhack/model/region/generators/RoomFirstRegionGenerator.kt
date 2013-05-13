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

import java.util.ArrayList
import java.util.Random
import net.wanhack.model.region.Cell
import net.wanhack.model.region.CellType
import net.wanhack.model.region.Door
import net.wanhack.model.region.Region
import net.wanhack.model.region.World
import net.wanhack.utils.Probability
import net.wanhack.utils.RandomUtils
import org.apache.commons.logging.LogFactory

class RoomFirstRegionGenerator: RegionGenerator {
    private var region: Region? = null
    private var width: Int = 0
    private var height: Int = 0
    private var minRooms: Int = 4
    private var maxRooms: Int = 10
    private var roomMinWidth: Int = 6
    private var roomMaxWidth: Int = 18
    private var roomMinHeight: Int = 5
    private var roomMaxHeight: Int = 9
    private val connectConnectedProbability = Probability(50)
    private val doorProbability = Probability(30)
    private val hiddenDoorProbability = Probability(15)
    private val overlapTries: Int = 20
    private val random = Random()
    private val log = LogFactory.getLog(javaClass)!!

    override fun generate(world: World, name: String, level: Int, up: String?, down: String?): Region {
        initRegionParameters(level)

        val region = Region(world, name, level, width, height)
        this.region = region
        val rooms = createRooms()
        createCorridors(rooms)
        createDoors()
        addStairsUpAndDown(rooms, up, down)
        return region
    }

    private fun initRegionParameters(level: Int) {
        if (level < 5) {
            width = 80
            height = 25
            minRooms = 4
            maxRooms = 10
            roomMinWidth = 6
            roomMaxWidth = 18
            roomMinHeight = 5
            roomMaxHeight = 9
        } else if (level < 10) {
            width = 120
            height = 30
            minRooms = 6
            maxRooms = 12
            roomMinWidth = 6
            roomMaxWidth = 18
            roomMinHeight = 5
            roomMaxHeight = 9
        } else if (level < 20) {
            width = 160
            height = 40
            minRooms = 8
            maxRooms = 16
            roomMinWidth = 6
            roomMaxWidth = 18
            roomMinHeight = 5
            roomMaxHeight = 9
        } else {
            width = 200
            height = 50
            minRooms = 10
            maxRooms = 25
            roomMinWidth = 6
            roomMaxWidth = 18
            roomMinHeight = 5
            roomMaxHeight = 9
        }
    }

    private fun createRooms(): List<Room> {
        val roomCount: Int = minRooms + random.nextInt(maxRooms - minRooms + 1)
        val rooms = ArrayList<Room>(roomCount)
        for (i in 0..roomCount - 1) {
            rooms.add(createRoom())
        }
        return rooms
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
        for (cell in region!!) {
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
        for (neighbour in cell.getAdjacentCellsInMainDirections()) {
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
        var count: Int = 0
        while (!allConnected(rooms)) {
            val room1 = RandomUtils.randomItem(rooms)
            val room2 = random(rooms, room1)
            if (!connected(room1, room2) || connectConnected())
            {
                connect(room1, room2)
            }

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
                for (adjacent in cell.getAdjacentCellsInMainDirections())
                    if (adjacent != previous && adjacent.isPassable())
                        return

            previous = cell
        }
    }
    private fun createPath(start: Cell, goal: Cell): List<Cell>? =
        CorridorPathSearcher(region!!).findShortestPath(start, goal)

    private fun random<T>(items: List<T>, invalid: T): T {
        var result = invalid
        while (result == invalid) {
            result = items[random.nextInt(items.size)]
        }
        return result
    }

    private fun allConnected(rooms: List<Room>): Boolean =
        rooms.all { connected(rooms[0], it) }

    private fun connected(room1: Room, room2: Room): Boolean =
        room1.getMiddleCell().isReachable(room2.getMiddleCell())

    private fun overlapsExisting(room: Room): Boolean {
        for (yy in 0..room.h - 1) {
            for (xx in 0..room.w - 1) {
                val cell = region!!.getCell(room.x + xx, room.y + yy)
                if (cell.cellType != CellType.WALL)
                    return true
            }
        }
        return false
    }

    fun addStairsUpAndDown(rooms: List<Room>, upRegion: String?, downRegion: String?): Unit {
        val stairsUpRoom = RandomUtils.randomItem(rooms)
        val empty = region!!.getRoomFloorCells()
        if (empty.size < 2)
            throw IllegalStateException("not enough empty cells to place stairs")

        val stairsUp = stairsUpRoom.getRandomCell(random)
        stairsUp.setType(CellType.STAIRS_UP)
        if (upRegion != null) {
            region?.addPortal(stairsUp.x, stairsUp.y, upRegion, "from down", true)
        }

        region?.addStartPoint("from up", stairsUp.x, stairsUp.y)

        if (downRegion != null) {
            while (true)
            {
                val stairsDownRoom = random(rooms, stairsUpRoom)
                val stairsDown = stairsDownRoom.getRandomCell(random)
                if (stairsDown != stairsUp && region?.findPath(stairsUp, stairsDown) != null) {
                    stairsDown.setType(CellType.STAIRS_DOWN)
                    region?.addPortal(stairsDown.x, stairsDown.y, downRegion, "from up", false)
                    region?.addStartPoint("from down", stairsDown.x, stairsDown.y)
                    return
                }
            }
        }
    }

    private fun randomRoom(): Room {
        val w = roomMinWidth + random.nextInt(1 + roomMaxWidth - roomMinWidth)
        val h = roomMinHeight + random.nextInt(1 + roomMaxHeight - roomMinHeight)
        val x = 1 + random.nextInt(width - w - 2)
        val y = 1 + random.nextInt(height - h - 2)
        return Room(region!!, x, y, w, h)
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
}
