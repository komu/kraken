package dev.komu.kraken.model.region.generators

import dev.komu.kraken.model.Direction
import dev.komu.kraken.model.region.*
import dev.komu.kraken.utils.Probability
import dev.komu.kraken.utils.logger
import dev.komu.kraken.utils.randomInt

class MazeRegionGenerator(world: World, val name: String, val level: Int, val up: String?, val down: String?) {
    private val region = Region(world, name, level, 80, 25)
    private val randomness = Probability(40)
    private val sparseness = 5
    private val deadEndsRemoved = Probability(90)
    private val roomCountRange = 2..8
    private val roomWidth = 3..10
    private val roomHeight = 3..7
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
        check(empty.size >= 2) { "not enough empty cells to place stairs" }

        val stairsUp = empty.randomElement()
        stairsUp.type = CellType.STAIRS_UP

        if (up != null)
            region.addPortal(stairsUp.coordinate, up, "from down", true)

        region.addStartPoint(stairsUp.coordinate, "from up")

        if (down != null) {
            val stairsDown = empty.findCellConnectedTo(stairsUp)
            if (stairsDown != null) {
                stairsDown.type = CellType.STAIRS_DOWN
                region.addPortal(stairsDown.coordinate, down, "from up", false)
                region.addStartPoint(stairsDown.coordinate, "from down")
                return
            } else {
                log.warning("failed to find cell for down-stairs")
            }
        }
    }

    private fun generateMaze() {
        val first = region[randomInt(1, region.width - 2), randomInt(1, region.height - 2)]
        first.type = CellType.HALLWAY_FLOOR

        val candidates = MutableCellSet(region)
        var current = first
        while (true) {
            current = generatePathFrom(current, candidates, null, 3, false) ?: candidates.randomElementOrNull() ?: break
        }
    }

    private fun generatePathFrom(
        current: Cell,
        candidates: MutableCellSet?,
        visited: MutableCellSet?,
        gridSize: Int,
        stopOnEmpty: Boolean
    ): Cell? {
        val currentCoordinate = current.coordinate

        for (dir in pathDirections()) {
            val xx = currentCoordinate.x + gridSize * dir.dx
            val yy = currentCoordinate.y + gridSize * dir.dy

            if (isCandidateForPath(xx, yy) && (visited == null || !visited.contains(xx, yy))) {
                val cell = region[xx, yy]
                if (!cell.isPassable && cell.type != CellType.UNDIGGABLE_WALL) {
                    for (i in 1 until gridSize)
                        region[currentCoordinate.x + i * dir.dx, currentCoordinate.y + i * dir.dy].type = CellType.HALLWAY_FLOOR

                    cell.type = CellType.HALLWAY_FLOOR

                    candidates?.add(cell)

                    return cell
                } else if (stopOnEmpty) {
                    for (i in 1 until gridSize)
                        region[currentCoordinate.x + i * dir.dx, currentCoordinate.y + i * dir.dy].type = CellType.HALLWAY_FLOOR

                    return null
                }
            }
        }

        candidates?.remove(current)

        return null
    }

    private fun pathDirections(): List<Direction> =
        if (randomness.check())
            Direction.mainDirections.shuffled()
        else
            Direction.mainDirections

    private fun sparsify() {
        repeat(sparseness) {
            val deadEnds = region.getMatchingCells(Cell::isDeadEnd)
            for (cell in deadEnds)
                cell.type = CellType.WALL
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
            visited += current
            current = generatePathFrom(current, null, visited, 3, true) ?: break
        }
    }

    private fun addRooms() {
        repeat(randomInt(roomCountRange)) {
            addRoom()
        }
    }

    private fun addRoom() {
        val width = randomInt(roomWidth)
        val height = randomInt(roomHeight)
        val x = 2 + randomInt(region.width - width - 4)
        val y = 2 + randomInt(region.height - height - 4)

        for (yy in y..y + height)
            for (xx in x..x + width)
                region[xx, yy].type = CellType.ROOM_FLOOR
    }

    private fun CellSet.findCellConnectedTo(start: Cell): Cell? {
        repeat(1000) {
            val cell = randomElement()
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
