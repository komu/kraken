/*
 * Copyright 2013 The Wanhack Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.wanhack.model.item.weapon

import net.wanhack.model.common.Attack
import net.wanhack.model.common.Color
import net.wanhack.model.creature.Creature
import net.wanhack.model.creature.Player
import net.wanhack.model.item.Equipable
import net.wanhack.utils.exp.Expression

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
