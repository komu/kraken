package dev.komu.kraken.model.common

import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.item.weapon.WeaponClass

interface Attack {
    fun getToHit(target: Creature): Int
    fun getDamage(target: Creature): Int
    val attackVerb: String
    val weaponClass: WeaponClass
}

fun findRollToHit(attacker: Creature, weapon: Attack, target: Creature): Int {
    val attackerLuck = attacker.luck
    val hitBonus = attacker.hitBonus
    val weaponToHit = weapon.getToHit(target)
    val proficiency = attacker.getProficiency(weapon.weaponClass)
    val armorClass = target.armorClass
    val targetLuck = target.luck
    return 1 + attackerLuck + hitBonus + weaponToHit + proficiency + armorClass - targetLuck
}

fun assignDamage(attacker: Creature, weapon: Attack, target: Creature) {
    val damage = weapon.getDamage(target)

    target.takeDamage(damage, attacker)
}
