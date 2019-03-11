package dev.komu.kraken.model.item.weapon

import dev.komu.kraken.model.common.Attack
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.utils.exp.Expression

class NaturalWeapon(private val verb: String, private val toHit: Int, private val damage: Expression): Attack {

    override val weaponClass = WeaponClass.NATURAL
    override val attackVerb = verb

    override fun getToHit(target: Creature) = toHit

    override fun getDamage(target: Creature) = damage.evaluate()

    override fun toString() = "NaturalWeapon [verb=$verb, toHit=$toHit, damage=$damage]"
}
