package dev.komu.kraken.common

import dev.komu.kraken.utils.randomElement

enum class Direction(val shortName: String, val dx: Int, val dy: Int) {
    NORTH("N", 0, -1),
    NE("NE", 1, -1),
    EAST("E", 1, 0),
    SE("SE", 1, 1),
    SOUTH("S", 0, 1),
    SW("SW", -1, 1),
    WEST("W", -1, 0),
    NW("NW", -1, -1);

    fun isOpposite(rhs: Direction) = dx == -rhs.dx && dy == -rhs.dy

    val isMain: Boolean
        get() = dx == 0 || dy == 0
}

object Directions {
    val directions = Direction.values().toList()
    val mainDirections = directions.filter { it.isMain }

    fun randomDirection() = directions.randomElement()

    fun forDeltas(dx: Int, dy: Int): Direction? =
        directions.find { d -> dx == d.dx && dy == d.dy }
}
