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

package net.wanhack.model.creature

import net.wanhack.model.region.Cell
import net.wanhack.model.region.ShortestPathSearcher

class CreatureShortestPathSearcher(val creature: Creature): ShortestPathSearcher(creature.region) {

    override fun costToEnter(cell: Cell): Int =
        when {
            cell.creature != null -> 5
            cell.isClosedDoor()   -> if (creature.canUseDoors) 2 else 10
            else                  -> 1
        }

    override fun canEnter(cell: Cell) =
        !creature.corporeal || cell.isPassable()
}
