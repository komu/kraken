package dev.komu.kraken.model.definitions

import dev.komu.kraken.model.common.Color
import dev.komu.kraken.model.item.Food
import dev.komu.kraken.model.item.Item
import dev.komu.kraken.model.item.armor.Armor
import dev.komu.kraken.model.item.armor.BodyPart
import dev.komu.kraken.model.item.weapon.Weapon
import dev.komu.kraken.model.item.weapon.WeaponClass
import dev.komu.kraken.utils.exp.Expression

open class ItemDefinition<T : Item>(val name: String, private val createItem: (String) -> T) : ObjectDefinition<T>() {

    override var level: Int? = null
    var color: Color? = null
    var weight: Int? = null
    var letter: Char? = null

    var createdInstances = 0
    var maximumInstances = Int.MAX_VALUE

    val instantiable: Boolean
        get() = createdInstances < maximumInstances

    override fun create(): T {
        val obj = createItem(name)

        setProperties(obj)

        createdInstances++
        return obj
    }

    open fun setProperties(obj: T) {
        if (level != null)
            obj.level = level!!

        if (letter != null)
            obj.letter = letter!!

        if (color != null)
            obj.color = color!!

        if (weight != null)
            obj.weight = weight!!
    }

    override fun toString() = "ItemDefinition [name=$name]"
}

class WeaponDefinition(name: String, weaponClass: WeaponClass) : ItemDefinition<Weapon>(name, { Weapon(it, weaponClass) }) {

    var toHit: Int? = null
    var damage: Expression? = null

    override fun setProperties(obj: Weapon) {
        super.setProperties(obj)

        if (toHit != null)
            obj.toHit = toHit!!

        if (damage != null)
            obj.damage = damage!!
    }
}

class ArmorDefinition(name: String) : ItemDefinition<Armor>(name, ::Armor) {

    var armorBonus: Int? = null
    var bodyPart: BodyPart? = null

    override fun setProperties(obj: Armor) {
        super.setProperties(obj)

        if (armorBonus != null)
            obj.armorBonus = armorBonus!!

        if (bodyPart != null)
            obj.bodyPart = bodyPart!!
    }
}

class FoodDefinition<T : Food>(name: String, create: (String) -> T) : ItemDefinition<T>(name, create) {

    var effectiveness: Int? = null
    var healingEffect: Int? = null

    override fun setProperties(obj: T) {
        super.setProperties(obj)

        if (effectiveness != null)
            obj.effectiveness = effectiveness!!

        if (healingEffect != null)
            obj.healingEffect = healingEffect!!
    }
}
