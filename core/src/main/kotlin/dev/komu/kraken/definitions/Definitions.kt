package dev.komu.kraken.definitions

import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.item.Item
import dev.komu.kraken.model.item.weapon.NaturalWeapon
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

    inline fun <T : Creature> creature(name: String, noinline create: () -> T, level: Int, init: CreatureDefinition<T>.() -> Unit = {}): CreatureDefinition<T> =
        CreatureDefinition(name, level, create).apply(init).also {
            creatureDefinitions += it
        }

    fun exp(exp: String) = Expression.parse(exp)
    fun random(exp: ClosedRange<Int>) = Expression.Apply("randint", listOf(Expression.Constant(exp.start), Expression.Constant(exp.endInclusive)))
    fun constant(value: Int) = Expression.Constant(value)
    fun hit(toHit: Int, exp: ClosedRange<Int>) = NaturalWeapon("hit", toHit, random(exp))
}
