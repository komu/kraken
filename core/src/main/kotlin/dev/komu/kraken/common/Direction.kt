/*
 * Copyright 2013 The Releasers of Kraken
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

    fun isOpposite(rhs: dev.komu.kraken.common.Direction) = dx == -rhs.dx && dy == -rhs.dy

    val isMain: Boolean
        get() = dx == 0 || dy == 0
}

object Directions {
    val directions = Direction.values().toList()
    val mainDirections = Directions.directions.filter { it.isMain }

    fun randomDirection() = dev.komu.kraken.common.Directions.directions.randomElement()

    fun forDeltas(dx: Int, dy: Int): Direction? =
        Directions.directions.find { d -> dx == d.dx && dy == d.dy }
}
