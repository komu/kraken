package dev.komu.kraken.model.creature

import dev.komu.kraken.model.item.Item
import dev.komu.kraken.model.item.food.Corpse

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
