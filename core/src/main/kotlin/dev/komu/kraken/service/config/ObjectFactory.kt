package dev.komu.kraken.service.config

import dev.komu.kraken.definitions.*
import dev.komu.kraken.model.creature.Creature
import java.util.*

class ObjectFactory {
    private val creatures = HashMap<String, CreatureDefinition<*>>()
    private val items = HashMap<String, ItemDefinition<*>>()

    val instantiableItems: Collection<ItemDefinition<*>>
        get() = items.values.filter { it.instantiable }

    val instantiableCreatures: Collection<CreatureDefinition<*>>
        get() = creatures.values.filter { it.instantiable }

    fun addDefinitions(definitions: Definitions) {
        for (definition in definitions.itemDefinitions)
            items[definition.name] = definition

        for (definition in definitions.creatureDefinitions)
            creatures[definition.name] = definition
    }

    fun createCreature(name: String) =
        getCreatureDefinition(name).create()

    fun createItem(name: String) =
        getItemDefinition(name).create()

    fun randomSwarm(regionLevel: Int, playerLevel: Int): Collection<Creature> {
        val minLevel = regionLevel / 6
        val maxLevel = (regionLevel + playerLevel) / 2

        return instantiableCreatures.betweenLevels(minLevel, maxLevel).weightedRandom().createSwarm()
    }

    private fun getItemDefinition(name: String) =
        items[name] ?: throw ConfigurationException("No such item <$name>")

    private fun getCreatureDefinition(name: String) =
        creatures[name] ?: throw ConfigurationException("No such creature <$name>")
}
