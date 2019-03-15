package dev.komu.kraken.model

import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.definitions.*
import java.util.*

class ObjectFactory {
    private val creatures = HashMap<String, MonsterDefinition>()
    private val items = HashMap<String, ItemDefinition<*>>()

    val instantiableItems: Collection<ItemDefinition<*>>
        get() = items.values.filter { it.instantiable }

    val instantiableMonsters: Collection<MonsterDefinition>
        get() = creatures.values.filter { it.instantiable }

    fun addDefinitions(definitions: ItemDefinitions) {
        for (definition in definitions.items)
            items[definition.name] = definition
    }

    fun addDefinitions(definitions: MonsterDefinitions) {
        for (definition in definitions.monsters)
            creatures[definition.name] = definition
    }

    fun createCreature(name: String) =
        getCreatureDefinition(name).create()

    fun createItem(name: String) =
        getItemDefinition(name).create()

    fun randomSwarm(regionLevel: Int, playerLevel: Int): Collection<Creature> {
        val minLevel = regionLevel / 6
        val maxLevel = (regionLevel + playerLevel) / 2

        return instantiableMonsters.betweenLevels(minLevel, maxLevel).weightedRandom().createSwarm()
    }

    private fun getItemDefinition(name: String) =
        items[name] ?: error("No such item <$name>")

    private fun getCreatureDefinition(name: String) =
        creatures[name] ?: error("No such creature <$name>")
}
