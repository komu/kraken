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

package net.wanhack.model.region

import java.lang.Math.*
import net.wanhack.model.common.Direction
import net.wanhack.utils.square
import net.wanhack.utils.signum

data class Coordinate(val x: Int, val y: Int) {
    fun plus(d: Direction) = Coordinate(x+d.dx, y+d.dy)
    fun distance(other: Coordinate) = sqrt((square(x - other.x) + square(y - other.y)).toDouble()).toInt()
    fun isAdjacent(other: Coordinate) = abs(x - other.x) < 2 && abs(y - other.y) < 2 && other != this

    fun directionOf(other: Coordinate): Direction {
        val dx = signum(other.x - x)
        val dy = signum(other.y - y)

        return Direction.values().find { dx == it.dx && dy == it.dy } ?:
            throw IllegalArgumentException("could not find direction of $other from $this")
    }
}
