package dev.komu.kraken.model

import dev.komu.kraken.model.item.Item
import java.util.*

class Inventory {

    val items = HashSet<Item>()

    inline fun <reified T : Any> byType() = items.filterIsInstance<T>()

    fun add(item: Item) {
        items.add(item)
    }

    fun remove(item: Item) {
        items.remove(item)
    }

    val weight: Int
        get() = items.sumBy(Item::weight)

    val lighting: Int
        get() = items.sumBy(Item::lighting)
}
