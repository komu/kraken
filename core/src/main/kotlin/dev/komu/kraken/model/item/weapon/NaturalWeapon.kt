package dev.komu.kraken.model.item.weapon

import dev.komu.kraken.model.common.Attack
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.utils.exp.Expression

open class NaturalWeapon(val verb: String, val toHit: Expression, val damage: Expression): Attack {

    constructor(verb: String, toHit: String, damage: String): this(verb, Expression.parse(toHit), Expression.parse(damage))

    override val weaponClass = WeaponClass.NATURAL
    override val attackVerb = verb

    override fun getToHit(target: Creature) = toHit.evaluate()

    override fun getDamage(target: Creature) = damage.evaluate()

    override fun toString() = "NaturalWeapon [verb=$verb, toHit=$toHit, damage=$damage]"
}
