package dev.komu.kraken.content

import dev.komu.kraken.model.definitions.ItemDefinition
import dev.komu.kraken.model.definitions.WeaponDefinition

/**
 * Queries and dumps contents
 */
fun main() {
    dumpItems(Weapons.items.filter { it.level in 3..5 })
    dumpItems(Items.items.filter { it.level in 3..5 })
}

fun dumpItems(items: Collection<ItemDefinition<*>>) {
    println("NAME                      LEVEL WEIGHT HIT DAMAGE")
    for (item in items) {
        val weapon = item as? WeaponDefinition
        System.out.printf("%-25s %5d %6d %3s %6s\n", item.name, item.level, item.weight, weapon?.toHit ?: "-", weapon?.damage ?: "-")
    }
    println()
}
