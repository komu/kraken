package dev.komu.kraken.model.region

import dev.komu.kraken.common.Direction
import dev.komu.kraken.common.Directions
import dev.komu.kraken.utils.signum
import dev.komu.kraken.utils.square
import java.lang.Math.abs
import java.lang.Math.sqrt

data class Coordinate(val x: Int, val y: Int) {
    operator fun plus(d: Direction) = Coordinate(x+d.dx, y+d.dy)
    fun distance(other: Coordinate) = sqrt((square(x - other.x) + square(y - other.y)).toDouble()).toInt()
    fun isAdjacent(other: Coordinate) = abs(x - other.x) < 2 && abs(y - other.y) < 2 && other != this

    fun directionOf(other: Coordinate): Direction {
        val dx = signum(other.x - x)
        val dy = signum(other.y - y)

        return Directions.forDeltas(dx, dy) ?:
            throw IllegalArgumentException("could not find direction of $other from $this")
    }
}
