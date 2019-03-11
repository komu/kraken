package dev.komu.kraken.model.item.weapon

import dev.komu.kraken.model.common.Attack
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.creature.Player
import dev.komu.kraken.model.item.Equipable
import dev.komu.kraken.utils.exp.Expression

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

    override fun equip(player: Player): Boolean {
        val oldWeapon = player.wieldedWeapon
        player.wieldedWeapon = this
        player.inventory.remove(this)
        if (oldWeapon != null) {
            player.message("You were wielding %s.", oldWeapon.title)
            player.inventory.add(oldWeapon)
        }

        player.message("You wield %s.", title)
        return true
    }
}
