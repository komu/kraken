package dev.komu.kraken.model

import dev.komu.kraken.model.common.Attack
import dev.komu.kraken.model.creature.Creature

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
    if (target.hitPoints <= 0) {
        attacker.message("%s %s.", target.You(), target.verb("die"))
        target.message("%s %s.", target.You(), target.verb("die"))
        target.die(attacker.name)
    }
}

