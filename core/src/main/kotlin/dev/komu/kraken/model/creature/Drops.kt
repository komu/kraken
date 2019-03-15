package dev.komu.kraken.model.creature

import dev.komu.kraken.definitions.Items
import dev.komu.kraken.model.item.Item
import dev.komu.kraken.model.item.food.Corpse
import dev.komu.kraken.utils.Probability
import dev.komu.kraken.utils.rollDie

/**
 * Allows monster drops to be customized.
 */
interface Drops {
    fun createDropsFor(creature: Creature): Collection<Item>
}

/**
 * Create a corpse for the monster.
 */
object CorpseDrops : Drops {
    override fun createDropsFor(creature: Creature): Collection<Item> =
        if (!creature.corporeal)
            emptyList()
        else
            listOf(Corpse("$${creature.name} corpse").apply {
                weight = creature.weight
                color = creature.color
                level = creature.level
                poisonDamage = creature.corpsePoisonousness
                effectiveness = (0.05 * creature.weight).toInt().coerceAtLeast(800)
                taste = creature.taste
            })
}

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
