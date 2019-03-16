package dev.komu.kraken.content

import dev.komu.kraken.model.definitions.ItemDefinition

/**
 * Queries and dumps contents
 */
fun main() {
    dumpItems(Weapons.items.filter { it.level in 3..5 })
}

fun dumpItems(items: Collection<ItemDefinition<*>>) {
    println("${items.size} items")

    println("NAME               LEVEL WEIGHT")
    for (item in items) {
        System.out.printf("%-18s %5d %6d\n", item.name, item.level, item.weight)
    }
}
