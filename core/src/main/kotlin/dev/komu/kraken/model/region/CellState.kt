package dev.komu.kraken.model.region

import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.creature.Player
import dev.komu.kraken.utils.Probability

interface CellState {
    val cellType: CellType
    fun search(searcher: Player): Boolean = false
    fun reveal() { }
}

class DefaultCellState(override val cellType: CellType): CellState

class Door(hidden: Boolean) : CellState {
    private var state = if (hidden) State.HIDDEN else State.CLOSED
    private val searchProbability = Probability(10)

    override fun search(searcher: Player): Boolean =
        if (state == State.HIDDEN && searchProbability.check()) {
            state = State.CLOSED
            searcher.message("%s %s a hidden door.", searcher.You(), searcher.verb("find"))
            true
        } else {
            false
        }

    override fun reveal() {
        if (state == State.HIDDEN)
            state = State.CLOSED
    }

    val isOpen: Boolean
        get() = state == State.OPEN

    override val cellType: CellType
        get() = state.cellType

    fun open(opener: Creature) {
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
        HIDDEN(CellType.ROOM_WALL),
        OPEN(CellType.OPEN_DOOR),
        CLOSED(CellType.CLOSED_DOOR)
    }
}
