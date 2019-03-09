package dev.komu.kraken.model.creature

import dev.komu.kraken.model.Energy
import dev.komu.kraken.model.Game
import dev.komu.kraken.model.actions.Action
import dev.komu.kraken.model.common.Attack
import dev.komu.kraken.model.item.weapon.NaturalWeapon
import dev.komu.kraken.model.region.Cell

open class Monster(name: String): Creature(name) {

    var lastKnownPlayerPosition: Cell? = null

    private var state: MonsterState = DefaultMonsterState()
    var naturalWeapon: Attack = NaturalWeapon("hit", "0", "randint(1, 3)")

    override var speed = Energy.NORMAL_SPEED

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

    override fun onAttackedBy(attacker: Creature) {
        if (attacker.isPlayer && state.isFriendly)
            state = DefaultMonsterState()
    }

    override val naturalAttack: Attack
        get() = naturalWeapon
}
