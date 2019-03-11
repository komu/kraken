package dev.komu.kraken.definitions

import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.item.Item
import dev.komu.kraken.model.item.food.Food
import dev.komu.kraken.model.item.weapon.NaturalWeapon
import dev.komu.kraken.model.item.weapon.WeaponClass
import dev.komu.kraken.utils.exp.Expression

abstract class Definitions {

    val itemDefinitions = mutableListOf<ItemDefinition<*>>()
    val creatureDefinitions = mutableListOf<CreatureDefinition<*>>()

    inline fun <T : Item> item(name: String, noinline create: (String) -> T, init: ItemDefinition<T>.() -> Unit = {}): ItemDefinition<T> =
        ItemDefinition(name) { create(name) }.apply(init).also {
            itemDefinitions += it
        }

    inline fun weapon(name: String, weaponClass: WeaponClass, init: WeaponDefinition.() -> Unit) =
        WeaponDefinition(name, weaponClass).apply(init).also {
            Weapons.itemDefinitions.add(it)
        }

    inline fun armor(name: String, init: ArmorDefinition.() -> Unit) =
        ArmorDefinition(name).apply(init).also {
            Weapons.itemDefinitions.add(it)
        }

    inline fun food(name: String, init: FoodDefinition<Food>.() -> Unit = {}): FoodDefinition<Food> =
        food(name, ::Food, init)

    inline fun <T : Food> food(name: String, noinline create: (String) -> T, init: FoodDefinition<T>.() -> Unit = {}): FoodDefinition<T> =
        FoodDefinition(name, create).apply(init).also {
            itemDefinitions += it
        }

    inline fun <T : Creature> creature(
        name: String,
        noinline create: (String) -> T,
        init: CreatureDefinition<T>.() -> Unit = {}
    ): CreatureDefinition<T> =
        CreatureDefinition(name, create).apply(init).also {
            creatureDefinitions += it
        }

    fun random(exp: ClosedRange<Int>) = Expression.random(exp)
    fun constant(value: Int) = Expression.constant(value)
    fun hit(toHit: Int, exp: ClosedRange<Int>) = NaturalWeapon("hit", toHit, random(exp))
}
