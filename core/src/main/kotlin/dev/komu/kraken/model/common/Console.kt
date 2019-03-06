package dev.komu.kraken.model.common

import dev.komu.kraken.common.Direction
import dev.komu.kraken.model.item.Item

interface Console {
    fun message(message: String)
    fun ask(question: String): Boolean
    fun <T: Item> selectItem(message: String, items: Collection<T>): T?
    fun selectDirection(): Direction?
}
