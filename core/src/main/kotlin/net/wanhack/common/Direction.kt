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

package net.wanhack.model.common

import net.wanhack.utils.collections.randomElement

enum class Direction(val shortName: String, val dx: Int, val dy: Int) {
    NORTH : Direction("N", 0, -1)
    NE : Direction("NE", 1, -1)
    EAST : Direction("E", 1, 0)
    SE : Direction("SE", 1, 1)
    SOUTH : Direction("S", 0, 1)
    SW : Direction("SW", -1, 1)
    WEST : Direction("W", -1, 0)
    NW : Direction("NW", -1, -1)

    fun isOpposite(rhs: Direction) = dx == -rhs.dx && dy == -rhs.dy
}

object Directions {
    val allDirections = Direction.values().toList()
    val mainDirections = listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)

    fun randomDirection() = Direction.values().randomElement()

    fun forDeltas(dx: Int, dy: Int): Direction? =
        Direction.values().find { d -> dx == d.dx && dy == d.dy }
}
