package dev.komu.kraken.model.item.weapon

import dev.komu.kraken.model.common.Attack
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.item.Equipable
import dev.komu.kraken.utils.Expression

class Weapon(name: String, override val weaponClass: WeaponClass): Equipable(name), Attack {

    override var attackVerb = "hit"

    var toHit = 0

    var damage = Expression.random(1..3)

    init {
        letter = '/'
        weight = 3
    }

    override val description: String
        get() = "(h: $toHit, d: $damage); ${super.description}"

    override fun getToHit(target: Creature) = toHit

    override fun getDamage(target: Creature) = damage.evaluate()

    override fun equip(creature: Creature): Boolean {
        val oldWeapon = creature.wieldedWeapon
        creature.wieldedWeapon = this
        creature.inventory.remove(this)
        if (oldWeapon != null) {
            creature.message("You were wielding %s.", oldWeapon.title)
            creature.inventory.add(oldWeapon)
        }

        creature.message("You wield %s.", title)
        return true
    }
}
