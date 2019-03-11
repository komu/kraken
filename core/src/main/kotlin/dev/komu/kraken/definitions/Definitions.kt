package dev.komu.kraken.definitions

import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.item.Item
import dev.komu.kraken.model.item.weapon.NaturalWeapon
import dev.komu.kraken.model.item.weapon.WeaponClass
import dev.komu.kraken.utils.exp.Expression

abstract class Definitions {

    val itemDefinitions = mutableListOf<ItemDefinition<*>>()
    val creatureDefinitions = mutableListOf<CreatureDefinition<*>>()

    inline fun weapon(name: String, weaponClass: WeaponClass, init: WeaponDefinition.() -> Unit) =
        WeaponDefinition(name, weaponClass).apply(init).also {
            Weapons.itemDefinitions.add(it)
        }

    inline fun <T : Item> item(name: String, noinline create: (String) -> T, init: ItemDefinition<T>.() -> Unit = {}): ItemDefinition<T> =
        ItemDefinition(name) { create(name) }.apply(init).also {
            itemDefinitions += it
        }

    inline fun <T : Creature> creature(
        name: String,
        noinline create: () -> T,
        level: Int,
        init: CreatureDefinition<T>.() -> Unit = {}
    ): CreatureDefinition<T> =
        CreatureDefinition(name, level, create).apply(init).also {
            creatureDefinitions += it
        }

    fun exp(exp: String) = Expression.parse(exp)
    fun random(exp: ClosedRange<Int>) =
        Expression.Apply("randint", listOf(Expression.Constant(exp.start), Expression.Constant(exp.endInclusive)))

    fun constant(value: Int) = Expression.Constant(value)
    fun hit(toHit: Int, exp: ClosedRange<Int>) = NaturalWeapon("hit", toHit, random(exp))
}
