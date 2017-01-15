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

package net.wanhack.model.item.armor

import net.wanhack.model.common.Color
import net.wanhack.model.creature.Player
import net.wanhack.model.item.Equipable

open class Armor(name: String): Equipable(name) {

    var armorBonus = 1
    var bodyPart = BodyPart.TORSO

    init {
        color = Color.BROWN
        letter = ']'
    }

    override val description: String
        get() = "ac: $armorBonus; ${super.description}"

    override fun equip(player: Player): Boolean {
        val oldArmor = player.replaceArmor(this)
        player.inventory.remove(this)
        if (oldArmor != null) {
            player.message("You were wearing %s.", oldArmor.title)
            player.inventory.add(oldArmor)
        }

        player.message("You are now wearing %s.", title)
        return true
    }
}
