package dev.komu.kraken.model.creature

import dev.komu.kraken.model.Game
import dev.komu.kraken.model.actions.Action
import dev.komu.kraken.model.actions.AttackAction
import dev.komu.kraken.model.actions.RandomMoveAction

interface MonsterState {
    fun act(self: Monster, game: Game): Pair<Action?, MonsterState>
    val isFriendly: Boolean
        get() = false
}

object FriendlyState : MonsterState {
    override fun act(self: Monster, game: Game): Pair<Action?, MonsterState> =
        RandomMoveAction(self) to this

    override val isFriendly = true
}

class DefaultMonsterState : MonsterState {

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
}
