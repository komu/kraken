package dev.komu.kraken.definitions

import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.item.Item
import dev.komu.kraken.utils.exp.Expression

abstract class Definitions {

    val itemDefinitions = mutableListOf<ItemDefinition<*>>()
    val creatureDefinitions = mutableListOf<CreatureDefinition<*>>()

    fun <T : Item> item(name: String,
                        level: Int? = null,
                        probability: Int? = null,
                        maximumInstances: Int? = null,
                        create: () -> T): ItemDefinition<T> {
        val def = ItemDefinition(name, create)

        if (level != null)
            def.level = level

        if (probability != null)
            def.probability = probability

        if (maximumInstances != null)
            def.maximumInstances = maximumInstances

        itemDefinitions.add(def)
        return def
    }

    fun <T : Creature> creature(name: String,
                                level: Int,
                                probability: Int? = null,
                                swarmSize: Expression? = null,
                                create: () -> T): CreatureDefinition<T> {
        val def = CreatureDefinition(name, level, create)

        if (probability != null)
            def.probability = probability

        if (swarmSize != null)
            def.swarmSize = swarmSize

        creatureDefinitions.add(def)
        return def
    }

    fun exp(exp: String) = Expression.parse(exp)
}
