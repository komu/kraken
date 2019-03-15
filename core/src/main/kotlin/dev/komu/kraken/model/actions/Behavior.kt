package dev.komu.kraken.model.actions

import dev.komu.kraken.model.Direction
import dev.komu.kraken.model.creature.Player

interface Behavior {
    fun canPerform(player: Player): Boolean
    fun getAction(player: Player): Action?
}

class ActionBehavior(private val action: Action) : Behavior {
    override fun canPerform(player: Player) = true
    override fun getAction(player: Player): Action {
        player.waitForInput()
        return action
    }
}

object RestBehavior : Behavior {

    override fun canPerform(player: Player): Boolean = when {
        player.hitPoints == player.maximumHitPoints -> {
            player.message("You feel rested")
            false
        }
        player.hungerLevel.hungry -> {
            player.message("You wake up feeling hungry.")
            false
        }
        player.seesNonFriendlyCreatures() -> { // TODO: more generic handling of disturbance
            player.message("Your rest is interrupted.")
            false
        }
        else ->
            true
    }

    override fun getAction(player: Player): Action = RestAction(player)
}

class RunInCorridorBehavior(private var direction: Direction) : Behavior {

    private var first = true

    override fun canPerform(player: Player): Boolean = when {
        first -> {
            first = false
            true
        }
        player.cell.isInteresting || player.seesNonFriendlyCreatures() || player.cell.countPassableMainNeighbours() > 2 ->
            false
        else -> {
            val next = Direction.mainDirections.singleOrNull {
                !it.isOpposite(direction) && player.cell.getCellTowards(it).canMoveInto(player.corporeal)
            }

            if (next != null) {
                direction = next
                true
            } else {
                false
            }
        }
    }

    override fun getAction(player: Player) = MoveAction(player, direction)
}

class RunInRoomBehavior(private var direction: Direction) : Behavior {

    private var first = true
    private var exit = false

    override fun canPerform(player: Player): Boolean = when {
        first -> {
            first = false
            true
        }
        exit || player.cell.isInteresting || player.seesNonFriendlyCreatures() ->
            false
        else -> {
            val target = player.cell.getCellTowards(direction)
            if (target.canMoveInto(player.corporeal)) {
                exit = player.cell.countPassableMainNeighbours() != target.countPassableMainNeighbours()
                true
            } else {
                false
            }
        }
    }

    override fun getAction(player: Player) = MoveAction(player, direction)
}
