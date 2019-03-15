package dev.komu.kraken.model.region

import dev.komu.kraken.model.Direction
import dev.komu.kraken.utils.square
import kotlin.math.abs
import kotlin.math.sign
import kotlin.math.sqrt

data class Coordinate(val x: Int, val y: Int) {
    operator fun plus(d: Direction) = Coordinate(x + d.dx, y + d.dy)

    fun distance(other: Coordinate) = sqrt((square(x - other.x) + square(y - other.y)).toDouble()).toInt()
    fun isAdjacent(other: Coordinate) = abs(x - other.x) < 2 && abs(y - other.y) < 2 && other != this

    fun directionOf(other: Coordinate): Direction {
        val dx = (other.x - x).sign
        val dy = (other.y - y).sign

        return Direction.forDeltas(dx, dy) ?: error("could not find direction of $other from $this")
    }
}
