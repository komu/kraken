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

import java.util.ArrayList
import java.util.EnumMap

class Armoring : Iterable<Armor> {
    private val armors = EnumMap<BodyPart, Armor>(javaClass<BodyPart>())

    override fun iterator(): Iterator<Armor> =
        armors.values().iterator()

    fun removeAllArmors(): Collection<Armor> {
        val result = ArrayList(armors.values())
        armors.clear()
        return result
    }

    val weight: Int
        get() {
            var w = 0

            for (armor in armors.values())
                w += armor.weight

            return w
        }

    val totalArmorBonus: Int
        get() {
            var bonus = 0
            for (armor in armors.values())
                bonus += armor.armorBonus
            return bonus
        }

    fun replaceArmor(armor: Armor): Armor? =
        armors.put(armor.bodyPart, armor)

    fun toString() = armors.values().toString()
}
