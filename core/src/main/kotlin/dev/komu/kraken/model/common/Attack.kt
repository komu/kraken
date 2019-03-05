package dev.komu.kraken.model.common

import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.item.weapon.WeaponClass

interface Attack {
    fun getToHit(target: Creature): Int
    fun getDamage(target: Creature): Int
    val attackVerb: String
    val weaponClass: WeaponClass
}
