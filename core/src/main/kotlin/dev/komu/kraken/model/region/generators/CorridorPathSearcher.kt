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

package dev.komu.kraken.model.region.generators

import dev.komu.kraken.model.region.Cell
import dev.komu.kraken.model.region.CellType.*
import dev.komu.kraken.model.region.Region
import dev.komu.kraken.model.region.ShortestPathSearcher

class CorridorPathSearcher(region: Region): ShortestPathSearcher(region) {

    override val allowSubDirections = false

    override fun canEnter(cell: Cell) = cell.cellType != UNDIGGABLE_WALL

    override fun costToEnter(cell: Cell): Int =
        when (cell.cellType) {
            UNDIGGABLE_WALL -> 100000
            ROOM_FLOOR      -> 100
            HALLWAY_FLOOR   -> 50
            ROOM_WALL       -> 200
            WALL            -> 100
            else            -> 500
        }
}
