package dev.komu.kraken.model.region.generators

import dev.komu.kraken.model.region.*
import dev.komu.kraken.utils.Probability
import dev.komu.kraken.utils.logger
import dev.komu.kraken.utils.randomElement
import dev.komu.kraken.utils.randomInt

class RoomFirstRegionGenerator private constructor(
    world: World,
    val name: String,
    val level: Int,
    private val rp: RegionParameters,
    val up: String?,
    val down: String?
) {
    private val region = Region(world, name, level, rp.width, rp.height)
    private val connectConnectedProbability = Probability(50)
    private val doorProbability = Probability(30)
    private val hiddenDoorProbability = Probability(15)
    private val overlapTries = 20
    private val log = javaClass.logger()

    fun generate(): Region {
        val rooms = List(randomInt(rp.rooms)) { createRoom() }
        createCorridors(rooms)
        createDoors()
        addStairsUpAndDown(rooms)
        return region
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
        val roomNeighbour = neighbors.find { it.cellType == CellType.ROOM_FLOOR }
        val hallwayNeighbour = neighbors.find { it.cellType == CellType.HALLWAY_FLOOR }
        val walls = neighbors.count { it.cellType == CellType.ROOM_WALL }

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

    private fun <T> random(items: List<T>, invalid: T): T {
        if (items.size == 1 && invalid in items)
            throw IllegalArgumentException("can't pick a random item without invalid from set consisting only invalid")

        var result = invalid
        while (result == invalid)
            result = items.randomElement()

        return result
    }

    private fun addStairsUpAndDown(rooms: List<Room>) {
        val stairsUpRoom = rooms.randomElement()
        val empty = region.getRoomFloorCells()

        check(empty.size >= 2) { "not enough empty cells to place stairs" }

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
        val w = randomInt(rp.roomWidth)
        val h = randomInt(rp.roomHeight)
        return Room(region, x = randomInt(1, region.width - w - 1), y = randomInt(1, region.height - h - 1), w = w, h = h)
    }

    private class Room(val region: Region, val x: Int, val y: Int, val w: Int, val h: Int) {
        operator fun contains(c: Coordinate) = c.x >= x && c.x < x + w && c.y >= y && c.y < y + h

        fun randomCell(): Cell {
            val xx = x + 1 + randomInt(w - 2)
            val yy = y + 1 + randomInt(h - 2)
            return region[xx, yy]
        }

        fun isConnectedTo(other: Room) =
            middleCell.isReachable(other.middleCell)

        val middleCell: Cell
            get() = region[x + w / 2, y + h / 2]

        fun addToRegion() {
            for (yy in 1 until h - 1)
                for (xx in 1 until w - 1)
                    region[x + xx, y + yy].setType(CellType.ROOM_FLOOR)

            for (xx in 0 until w) {
                region[x + xx, y].setType(CellType.ROOM_WALL)
                region[x + xx, y + h - 1].setType(CellType.ROOM_WALL)
            }
            for (yy in 0 until h) {
                region[x, y + yy].setType(CellType.ROOM_WALL)
                region[x + w - 1, y + yy].setType(CellType.ROOM_WALL)
            }
        }

        fun overlapsExisting(): Boolean {
            for (yy in y until y + h)
                for (xx in x until x + w)
                    if (region[xx, yy].cellType != CellType.WALL)
                        return true

            return false
        }
    }

    companion object : RegionGenerator {
        override fun generate(world: World, name: String, level: Int, up: String?, down: String?): Region =
            RoomFirstRegionGenerator(world, name, level, regionParameters(level), up, down).generate()

        private fun regionParameters(level: Int) = when {
            level < 5 -> RegionParameters(width = 80, height = 25, rooms = 4..10, roomWidth = 6..18, roomHeight = 5..9)
            level < 10 -> RegionParameters(width = 120, height = 30, rooms = 6..12, roomWidth = 6..18, roomHeight = 5..9)
            level < 20 -> RegionParameters(width = 160, height = 40, rooms = 8..16, roomWidth = 6..18, roomHeight = 5..9)
            else -> RegionParameters(width = 200, height = 50, rooms = 10..25, roomWidth = 6..18, roomHeight = 5..9)
        }
    }

    private class RegionParameters(
        val width: Int,
        val height: Int,
        val rooms: ClosedRange<Int>,
        val roomWidth: ClosedRange<Int>,
        val roomHeight: ClosedRange<Int>
    )
}
