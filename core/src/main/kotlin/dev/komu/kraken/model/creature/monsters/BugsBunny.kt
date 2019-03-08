package dev.komu.kraken.model.creature.monsters

import dev.komu.kraken.common.Direction
import dev.komu.kraken.model.Game
import dev.komu.kraken.model.actions.Action
import dev.komu.kraken.model.actions.RandomMoveAction
import dev.komu.kraken.model.common.Color
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.creature.Monster
import dev.komu.kraken.model.item.weapon.NaturalWeapon
import dev.komu.kraken.utils.randomInt

class BugsBunny: Monster("Bugs Bunny") {

    init {
        level = 20
        hitPoints = randomInt(100, 200)
        letter = 'r'
        color = Color.WHITE
        luck = 2
        naturalWeapon = NaturalWeapon("hit", "10", "randint(4,10)")
        killExperience = 450
        armorClass = 0
        speed = 9
        weight = 4500
    }

    override fun talk(target: Creature) {
        target.say(this, "What's up, Doc?")
    }

    override fun getAction(game: Game): Action? {
        if (seesCreature(game.player))
            game.movePlayer(Direction.randomDirection())

        return RandomMoveAction(this)
    }
}
