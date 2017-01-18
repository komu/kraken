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

package net.wanhack.model.item

import net.wanhack.model.common.Attack
import net.wanhack.model.common.Color
import net.wanhack.model.creature.Creature
import net.wanhack.model.item.weapon.WeaponClass
import net.wanhack.utils.rollDie

open class Item(title: String): Attack {

    var unidentifiedTitle = title
    var identifiedTitle = title
    var letter = '*'
    var weight = 1
    var level = 1
    var color = Color.BLACK
    var identified = false

    val title: String
        get() = if (identified) identifiedTitle else unidentifiedTitle

    open val description: String
        get() = "weight=$weight"

    open val lighting = 0

    override fun toString() = title

    override val attackVerb = "hit"
    override val weaponClass = WeaponClass.NOT_WEAPON

    override fun getToHit(target: Creature) = -2

    override fun getDamage(target: Creature) =
        when {
            weight > 5000 -> rollDie(3)
            weight > 1000 -> rollDie(2)
            else          -> 1
        }
}
