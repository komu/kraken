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
import net.wanhack.model.item.Item
import net.wanhack.model.item.Equipable
import net.wanhack.model.creature.Player

public abstract class Weapon(name: String): Item(name), Attack, Equipable {

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
