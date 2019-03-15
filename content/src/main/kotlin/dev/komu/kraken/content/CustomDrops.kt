package dev.komu.kraken.content

import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.creature.Drops
import dev.komu.kraken.model.item.Item
import dev.komu.kraken.utils.Probability
import dev.komu.kraken.utils.rollDie

/**
 * Drop cheese instead of corpse.
 */
object CheeseDrops : Drops {
    override fun createDropsFor(creature: Creature): Collection<Item> =
        listOf(if (Probability.check(50)) Items.chunkOfCheese.create() else Items.bigChunkOfCheese.create())
}

/**
 * Drop wraith essence or drags instead of corpse.
 */
object WraithDrops : Drops {
    override fun createDropsFor(creature: Creature): Collection<Item> =
        listOf(if (Probability.check(10)) {
            Items.wraithEssence.create().apply {
                healingEffect = rollDie(creature.killExperience)
            }
        } else {
            Items.oldRags.create().apply {
                color = creature.color
            }
        })
}
