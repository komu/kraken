package dev.komu.kraken.content

import dev.komu.kraken.model.Game
import dev.komu.kraken.model.actions.Action
import dev.komu.kraken.model.actions.AttackAction
import dev.komu.kraken.model.actions.RandomMoveAction
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.creature.Monster
import dev.komu.kraken.model.creature.MonsterState
import dev.komu.kraken.model.item.weapon.NaturalWeapon
import dev.komu.kraken.utils.Expression
import dev.komu.kraken.utils.Probability
import dev.komu.kraken.utils.randomItem

enum class PetType(private val petName: String) {
    DORIS("Doris"),
    LASSIE("Lassie");

    fun instantiate(): Monster = when (this) {
        DORIS -> Monster("Doris").apply {
            weight = 10
            letter = 'f'
            naturalWeapon = NaturalWeapon("bite", 1, Expression.random(3..7))
            state = PetState.Doris
        }
        LASSIE -> Monster("Lassie").apply {
            weight = 25
            letter = 'C'
            naturalWeapon = NaturalWeapon("bite", 1, Expression.random(3..7))
            state = PetState.Lassie
        }
    }

    override fun toString() = petName
}

sealed class PetState : MonsterState {

    override val comesAlongInSteps: Boolean
        get() = true

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

    /**
     * A pet that attacks the owner every now and then.
     */
    object Doris : PetState() {

        override fun talk(self: Monster, target: Creature) {
            val verb = randomItem("meows", "purrs")
            target.message("%s %s.", self.name, verb)
        }

        override fun act(self: Monster, game: Game): Pair<Action?, MonsterState> {
            val player = game.player
            return if (self.isAdjacentToCreature(player) && Probability.check(percentage = 1))
                AttackAction(player, self) to this
            else
                super.act(self, game)
        }
    }

    /**
     * A pet that tries to return home.
     */
    object Lassie : PetState() {

        override fun talk(self: Monster, target: Creature) {
            target.message("$${self.name} barks.")
        }

        override fun act(self: Monster, game: Game): Pair<Action?, MonsterState> {
            val escape = self.region.find { it.getJumpTarget(true)?.isExit ?: false }

            return when {
                escape == self.cell -> {
                    self.hitPoints = 0
                    self.removeFromGame()
                    game.message("$${self.name} went home.")
                    null to this
                }
                escape != null ->
                    self.moveTowardsAction(escape)?.let { it to this } ?: super.act(self, game)
                else ->
                    super.act(self, game)
            }
        }
    }
}

