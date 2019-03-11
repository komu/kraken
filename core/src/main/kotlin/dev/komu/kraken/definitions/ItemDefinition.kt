package dev.komu.kraken.definitions

import dev.komu.kraken.model.common.Color
import dev.komu.kraken.model.item.Item

class ItemDefinition<T : Item>(val name: String, val createItem: () -> T) : ObjectDefinition<T>() {

    override var level: Int? = null
    var color: Color? = null
    var weight: Int? = null
    var letter: Char? = null

    var createdInstances = 0
    var maximumInstances = Integer.MAX_VALUE
    private val initHooks = mutableListOf<T.() -> Unit>()

    val instantiable: Boolean
        get() = createdInstances < maximumInstances

    override fun create(): T {
        val obj = createItem()

        if (letter != null)
            obj.letter = letter!!

        if (color != null)
            obj.color = color!!

        if (weight != null)
            obj.weight = weight!!

        for (hook in initHooks)
            obj.hook()

        createdInstances++
        return obj
    }

    fun init(hook: T.() -> Unit): ItemDefinition<T> {
        initHooks.add(hook)
        return this
    }

    override fun toString() = "ItemDefinition [name=$name]"
}
