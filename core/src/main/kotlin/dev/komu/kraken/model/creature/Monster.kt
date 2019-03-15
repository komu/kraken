package dev.komu.kraken.model.creature

import dev.komu.kraken.model.Game
import dev.komu.kraken.model.actions.Action
import dev.komu.kraken.model.region.Cell

class Monster(name: String): Creature(name) {

    var lastKnownPlayerPosition: Cell? = null

    var state: MonsterState = DefaultMonsterState.INSTANCE

    override val isFriendly: Boolean
        get() = state.isFriendly

    override fun getAction(game: Game): Action? {
        val seesPlayer = seesCreature(game.player)

        if (seesPlayer)
            lastKnownPlayerPosition = game.player.cell
        else if (cell == lastKnownPlayerPosition)
            lastKnownPlayerPosition = null

        val (action, nextState) = state.act(this, game)
        state = nextState
        return action
    }

    override fun talk(target: Creature) {
        state.talk(this, target)
    }

    override fun onAttackedBy(attacker: Creature) {
        if (attacker.isPlayer && state.isFriendly)
            state = DefaultMonsterState.INSTANCE
    }

    override fun didTakeDamage(points: Int, attacker: Creature) {
        state.didTakeDamage(this, points, attacker)
    }
}
