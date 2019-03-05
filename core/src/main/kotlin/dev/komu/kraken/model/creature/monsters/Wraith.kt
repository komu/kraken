package dev.komu.kraken.model.creature.monsters

import dev.komu.kraken.definitions.Items
import dev.komu.kraken.model.common.Color
import dev.komu.kraken.model.creature.Monster
import dev.komu.kraken.utils.Probability
import dev.komu.kraken.utils.rollDie

class Wraith(name: String): Monster(name) {

    init {
        letter = 'W'
        color = Color.BLACK
        canUseDoors = true
    }

    override fun createCorpse() =
        if (Probability.check(10)) {
            val essence = Items.wraithEssence.create()
            essence.healingEffect = rollDie(killExperience)
            essence
        } else {
            val rags = Items.oldRags.create()
            rags.color = color
            rags
        }
}
