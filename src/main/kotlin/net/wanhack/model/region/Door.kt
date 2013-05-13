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

import net.wanhack.model.creature.Creature
import net.wanhack.model.creature.Player
import net.wanhack.utils.Probability

class Door(hidden: Boolean): CellState {
    private var state = if (hidden) State.HIDDEN else State.CLOSED
    private val searchProbability = Probability(10)

    override fun search(searcher: Player): Boolean {
        if (state == State.HIDDEN && searchProbability.check()) {
            state = State.CLOSED
            searcher.message("%s %s a hidden door.", searcher.You(), searcher.verb("find"))
            return true
        } else {
            return false
        }
    }

    val isOpen: Boolean
        get() = state == State.OPEN

    override val cellType: CellType
        get() = state.cellType

    fun open(opener: Creature)  {
        if (state == State.CLOSED) {
            if (Probability.check(opener.strength)) {
                state = State.OPEN
                opener.message("Opened door.")
            } else {
                opener.message("The door resists.")
            }
        }
    }

    fun close(closer: Creature) {
        if (state == State.OPEN) {
            if (Probability.check(closer.strength)) {
                state = State.CLOSED
                closer.message("Closed door.")
            } else {
                closer.message("The door resists.")
            }
        }
    }

    enum class State(val cellType: CellType) {
        HIDDEN : State(CellType.ROOM_WALL)
        OPEN : State(CellType.OPEN_DOOR)
        CLOSED : State(CellType.CLOSED_DOOR)
    }
}
