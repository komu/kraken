package dev.komu.kraken.model.definitions

import dev.komu.kraken.model.item.Food
import dev.komu.kraken.model.item.Item
import dev.komu.kraken.model.item.weapon.NaturalWeapon
import dev.komu.kraken.model.item.weapon.WeaponClass
import dev.komu.kraken.utils.Expression

abstract class Definitions {

    fun random(exp: ClosedRange<Int>) = Expression.random(exp)
    fun constant(value: Int) = Expression.constant(value)
    fun hit(toHit: Int, exp: ClosedRange<Int>) = NaturalWeapon("hit", toHit, random(exp))
}

abstract class ItemDefinitions : Definitions() {

    val items = mutableListOf<ItemDefinition<*>>()

    inline fun <T : Item> item(name: String, noinline create: (String) -> T, init: ItemDefinition<T>.() -> Unit = {}): ItemDefinition<T> =
        ItemDefinition(name) { create(name) }.apply(init).also {
            items += it
        }

    inline fun weapon(name: String, weaponClass: WeaponClass, init: WeaponDefinition.() -> Unit) =
        WeaponDefinition(name, weaponClass).apply(init).also {
            items.add(it)
        }

    inline fun armor(name: String, init: ArmorDefinition.() -> Unit) =
        ArmorDefinition(name).apply(init).also {
            items.add(it)
        }

    inline fun food(name: String, init: FoodDefinition<Food>.() -> Unit = {}): FoodDefinition<Food> =
        food(name, ::Food, init)

    inline fun <T : Food> food(name: String, noinline create: (String) -> T, init: FoodDefinition<T>.() -> Unit = {}): FoodDefinition<T> =
        FoodDefinition(name, create).apply(init).also {
            items += it
        }

}

abstract class MonsterDefinitions : Definitions() {
    val monsters = mutableListOf<MonsterDefinition>()

    inline fun monster(name: String, init: MonsterDefinition.() -> Unit) =
        MonsterDefinition(name).apply(init).also {
            monsters += it
        }
}
