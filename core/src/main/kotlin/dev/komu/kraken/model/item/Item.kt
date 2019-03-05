package dev.komu.kraken.model.item

import dev.komu.kraken.model.common.Attack
import dev.komu.kraken.model.common.Color
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.item.weapon.WeaponClass
import dev.komu.kraken.utils.rollDie

open class Item(title: String): Attack {

    var unidentifiedTitle = title
    var identifiedTitle = title
    var letter = '*'
    var weight = 1
    var level = 1
    var color = Color.BLACK
    var identified = false

    val title: String
        get() = if (identified) identifiedTitle else unidentifiedTitle

    open val description: String
        get() = "weight=$weight"

    open val lighting = 0

    override fun toString() = title

    override val attackVerb = "hit"
    override val weaponClass = WeaponClass.NOT_WEAPON

    override fun getToHit(target: Creature) = -2

    override fun getDamage(target: Creature) =
        when {
            weight > 5000 -> rollDie(3)
            weight > 1000 -> rollDie(2)
            else          -> 1
        }
}
