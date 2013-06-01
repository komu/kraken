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

import net.wanhack.model.region.Cell
import net.wanhack.model.region.CellType
import net.wanhack.model.region.Door
import net.wanhack.model.region.Region
import net.wanhack.model.region.World
import net.wanhack.utils.Probability
import net.wanhack.service.region.generators.RoomFirstRegionGenerator.RegionParameters
import net.wanhack.utils.collections.randomElement
import net.wanhack.utils.logger
import net.wanhack.utils.RandomUtils
import net.wanhack.model.region.Coordinate

class RoomFirstRegionGenerator(val world: World, val name: String, val level: Int, val rp: RegionParameters, val up: String?, val down: String?) {
    private val region = Region(world, name, level, rp.width, rp.height)
    private val connectConnectedProbability = Probability(50)
    private val doorProbability = Probability(30)
    private val hiddenDoorProbability = Probability(15)
    private val overlapTries = 20
    private val log = javaClass.logger()

    fun generate(): Region {
        val rooms = createRooms()
        createCorridors(rooms)
        createDoors()
        addStairsUpAndDown(rooms)
        return region
    }

    private fun createRooms(): List<Room> {
        val rooms = listBuilder<Room>()

        RandomUtils.randomInt(rp.minRooms, rp.maxRooms).times {
            rooms.add(createRoom())
        }

        return rooms.build()
    }

    private fun createRoom(): Room {
        var room = randomRoom()
        var tries = overlapTries
        while (room.overlapsExisting() && tries-- > 0)
            room = randomRoom()

        room.addToRegion()
        return room
    }

    private fun createDoors() {
        for (cell in region)
            if (isDoorCandidate(cell) && doorProbability.check())
                cell.state = Door(hiddenDoorProbability.check())
    }

    private fun isDoorCandidate(cell: Cell): Boolean {
        if (cell.cellType != CellType.HALLWAY_FLOOR)
            return false

        val neighbors = cell.adjacentCellsInMainDirections
        val roomNeighbour    = neighbors.find { it.cellType == CellType.ROOM_FLOOR }
        val hallwayNeighbour = neighbors.find { it.cellType == CellType.HALLWAY_FLOOR }
        val walls            = neighbors.count { it.cellType == CellType.ROOM_WALL }

        if (roomNeighbour != null && hallwayNeighbour != null && walls == 2) {
            val room = cell.getDirection(roomNeighbour)
            val hall = cell.getDirection(hallwayNeighbour)
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
            if (!room1.isConnectedTo(room2) || connectConnected())
                connect(room1, room2)

            if (count > 1000) {
                log.warning("Count exceeded, bailing out.")
                break
            }
            count++
        }
    }

    private fun allConnected(rooms: List<Room>) =
        rooms.all { rooms.first().isConnectedTo(it) }

    private fun connectConnected() =
        connectConnectedProbability.check()

    private fun connect(start: Room, goal: Room) {
        val startCell = start.randomCell()
        val goalCell = goal.randomCell()

        val path = CorridorPathSearcher(region).findShortestPath(startCell, goalCell)!!
        var previous: Cell? = null
        for (cell in path) {
            if (cell.cellType != CellType.ROOM_FLOOR)
                cell.setType(CellType.HALLWAY_FLOOR)

            if (cell.coordinate !in start)
                for (adjacent in cell.adjacentCellsInMainDirections)
                    if (adjacent != previous && adjacent.isPassable)
                        return

            previous = cell
        }
    }

    private fun random<T>(items: List<T>, invalid: T): T {
        if (items.size == 1 && invalid in items)
            throw IllegalArgumentException("can't pick a random item without invalid from set consisting only invalid")

        var result = invalid
        while (result == invalid)
            result = items.randomElement()

        return result
    }

    fun addStairsUpAndDown(rooms: List<Room>) {
        val stairsUpRoom = rooms.randomElement()
        val empty = region.getRoomFloorCells()
        if (empty.size < 2)
            throw IllegalStateException("not enough empty cells to place stairs")

        val stairsUp = stairsUpRoom.randomCell()
        stairsUp.setType(CellType.STAIRS_UP)
        if (up != null)
            region.addPortal(stairsUp.coordinate, up, "from down", true)

        region.addStartPoint(stairsUp.coordinate, "from up")

        if (down != null) {
            while (true) {
                val stairsDownRoom = random(rooms, stairsUpRoom)
                val stairsDown = stairsDownRoom.randomCell()
                if (stairsDown != stairsUp && region.findPath(stairsUp, stairsDown) != null) {
                    stairsDown.setType(CellType.STAIRS_DOWN)
                    region.addPortal(stairsDown.coordinate, down, "from up", false)
                    region.addStartPoint(stairsDown.coordinate, "from down")
                    return
                }
            }
        }
    }

    private fun randomRoom(): Room {
        val w = RandomUtils.randomInt(rp.roomMinWidth, rp.roomMaxWidth)
        val h = RandomUtils.randomInt(rp.roomMinHeight, rp.roomMaxHeight)
        val x = RandomUtils.randomInt(1, region.width - w - 1)
        val y = RandomUtils.randomInt(1, region.height - h - 1)
        return Room(region, x, y, w, h)
    }

    private class Room(val region: Region, val x: Int, val y: Int, val w: Int, val h: Int) {
        fun contains(c: Coordinate) = c.x >= x && c.x < x + w && c.y >= y && c.y < y + h

        fun randomCell(): Cell {
            val xx = x + 1 + RandomUtils.randomInt(w - 2)
            val yy = y + 1 + RandomUtils.randomInt(h - 2)
            return region[xx, yy]
        }

        fun isConnectedTo(other: Room) =
            middleCell.isReachable(other.middleCell)

        val middleCell: Cell
            get() = region[x + w / 2, y + h / 2]

        fun addToRegion() {
            for (yy in 1..h - 1 - 1)
                for (xx in 1..w - 1 - 1)
                    region[x + xx, y + yy].setType(CellType.ROOM_FLOOR)

            for (xx in 0..w - 1) {
                region[x + xx, y].setType(CellType.ROOM_WALL)
                region[x + xx, y + h - 1].setType(CellType.ROOM_WALL)
            }
            for (yy in 0..h - 1) {
                region[x, y + yy].setType(CellType.ROOM_WALL)
                region[x + w - 1, y + yy].setType(CellType.ROOM_WALL)
            }
        }

        fun overlapsExisting(): Boolean {
            for (yy in y..y + h - 1)
                for (xx in x..x + w - 1)
                    if (region[xx, yy].cellType != CellType.WALL)
                        return true

            return false
        }
    }

    class object : RegionGenerator {
        override fun generate(world: World, name: String, level: Int, up: String?, down: String?): Region =
            RoomFirstRegionGenerator(world, name, level, regionParameters(level), up, down).generate()

        fun regionParameters(level: Int): RegionParameters {
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
