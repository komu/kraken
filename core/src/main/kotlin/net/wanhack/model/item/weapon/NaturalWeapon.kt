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
import net.wanhack.model.creature.Creature
import net.wanhack.utils.exp.Expression

open class NaturalWeapon(val verb: String, val toHit: Expression, val damage: Expression): Attack {

    override val weaponClass = WeaponClass.NATURAL
    override val attackVerb = verb

    override fun getToHit(target: Creature) = toHit.evaluate()

    override fun getDamage(target: Creature) = damage.evaluate()

    override fun toString() = "NaturalWeapon [verb=$verb, toHit=$toHit, damage=$damage]"
}

fun NaturalWeapon(verb: String, toHit: String, damage: String) = NaturalWeapon(verb, Expression.parse(toHit), Expression.parse(damage))
