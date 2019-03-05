package dev.komu.kraken.model.item.weapon

import dev.komu.kraken.model.common.Attack
import dev.komu.kraken.model.common.Color
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.creature.Player
import dev.komu.kraken.model.item.Equipable
import dev.komu.kraken.utils.exp.Expression

abstract class Weapon(name: String): Equipable(name), Attack {

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

abstract class BasicWeapon(name: String, override val weaponClass: WeaponClass, damage: String): Weapon(name) {
    override var attackVerb = "hit"

    var toHit = Expression.parse("0")

    var damage = Expression.parse(damage)

    init {
        letter = '/'
        weight = 3
    }

    override val description: String
        get() = "(h: $toHit, d: $damage); ${super.description}"

    override fun getToHit(target: Creature) = toHit.evaluate()

    override fun getDamage(target: Creature) = damage.evaluate()
}

open class BluntWeapon(name: String): BasicWeapon(name, WeaponClass.BLUNT, "randint(1, 3)")

open class MissileLauncher(name: String): BasicWeapon(name, WeaponClass.LAUNCHER, "randint(1, 6)")

open class PointedWeapon(name: String): BasicWeapon(name, WeaponClass.SPEAR, "randint(1, 6)")

open class Projectile(name: String): BasicWeapon(name, WeaponClass.PROJECTILE, "randint(1, 6)")

open class Sword(name: String): BasicWeapon(name, WeaponClass.SWORD, "randint(1, 6)") {
    init {
        damage = Expression.parse("1d3")
        letter = '†'
        color = Color.DARK_GRAY
    }
}
