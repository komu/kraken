package dev.komu.kraken.model.creature

import dev.komu.kraken.model.Game
import dev.komu.kraken.model.actions.Action
import dev.komu.kraken.model.actions.AttackAction
import dev.komu.kraken.model.actions.RandomMoveAction
import dev.komu.kraken.utils.Probability

interface MonsterState {
    fun act(self: Monster, game: Game): Pair<Action?, MonsterState>
    val isFriendly: Boolean
        get() = false
}

object PetState : MonsterState {
    override fun act(self: Monster, game: Game): Pair<Action?, MonsterState> {
        val player = game.player

        val enemy = self.adjacentCreatures.find { !it.isFriendly }
        val lastKnownPlayerPosition = self.lastKnownPlayerPosition
        return when {
            enemy != null ->
                AttackAction(enemy, self)
            self.isAdjacentToCreature(player) ->
                RandomMoveAction(self)
            self.seesCreature(player) && Probability.check(50) ->
                RandomMoveAction(self)
            lastKnownPlayerPosition != null ->
                self.moveTowardsAction(lastKnownPlayerPosition)
            else ->
                RandomMoveAction(self)
        } to this
    }

    override val isFriendly = true
}

object DefaultMonsterState : MonsterState {

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
