package dev.komu.kraken.definitions

import dev.komu.kraken.model.common.Color
import dev.komu.kraken.model.item.Item
import dev.komu.kraken.model.item.weapon.Weapon
import dev.komu.kraken.model.item.weapon.WeaponClass
import dev.komu.kraken.utils.exp.Expression

open class ItemDefinition<T : Item>(val name: String, val createItem: () -> T) : ObjectDefinition<T>() {

    override var level: Int? = null
    var color: Color? = null
    var weight: Int? = null
    var letter: Char? = null

    var createdInstances = 0
    var maximumInstances = Int.MAX_VALUE
    private val initHooks = mutableListOf<T.() -> Unit>()

    val instantiable: Boolean
        get() = createdInstances < maximumInstances

    override fun create(): T {
        val obj = createItem()

        setProperties(obj)

        for (hook in initHooks)
            obj.hook()

        createdInstances++
        return obj
    }

    open fun setProperties(obj: T) {
        if (letter != null)
            obj.letter = letter!!

        if (color != null)
            obj.color = color!!

        if (weight != null)
            obj.weight = weight!!
    }

    fun init(hook: T.() -> Unit): ItemDefinition<T> {
        initHooks.add(hook)
        return this
    }

    override fun toString() = "ItemDefinition [name=$name]"
}

class WeaponDefinition(name: String, weaponClass: WeaponClass) : ItemDefinition<Weapon>(name, { Weapon(name, weaponClass) }) {

    var toHit: Expression? = null
    var damage: Expression? = null

    override fun setProperties(obj: Weapon) {
        super.setProperties(obj)

        if (toHit != null)
            obj.toHit = toHit!!

        if (damage != null)
            obj.damage = damage!!
    }
}
