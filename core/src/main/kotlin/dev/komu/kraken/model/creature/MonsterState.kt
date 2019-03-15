package dev.komu.kraken.model.creature

import dev.komu.kraken.model.Game
import dev.komu.kraken.model.actions.Action
import dev.komu.kraken.model.actions.AttackAction
import dev.komu.kraken.model.actions.RandomMoveAction

/**
 * Defines the AI for creatures.
 */
interface MonsterState {

    /**
     * Evaluate what to do next. Return a pair of action to perform and the next state.
     */
    fun act(self: Monster, game: Game): Pair<Action?, MonsterState>

    fun talk(self: Monster, target: Creature) {
        target.say(self, "Hrmph.")
    }

    /**
     * Called after monster has taken damage, but before checking if it has died.
     */
    fun didTakeDamage(self: Monster, points: Int, attacker: Creature) {
    }

    /**
     * Are creatures in this state friendly towards player?
     */
    val isFriendly: Boolean
        get() = false
}

open class DefaultMonsterState : MonsterState {

    override fun act(self: Monster, game: Game): Pair<Action?, MonsterState> {
        val player = game.player

        val playerPosition = self.lastKnownPlayerPosition
        return when {
            self.isAdjacentToCreature(player) ->
                AttackAction(player, self)
            self.immobile ->
                null
            playerPosition != null ->
                self.moveTowardsAction(playerPosition)
            !self.immobile ->
                self.moveTowardsAction(player.cell)
            else ->
                RandomMoveAction(self)
        } to this
    }

    companion object {
        val INSTANCE = DefaultMonsterState()
    }
}
