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

package net.wanhack.model.skill

import java.util.EnumMap
import net.wanhack.common.MessageTarget
import net.wanhack.model.item.weapon.WeaponClass

class SkillSet {
    private val weaponSkills = EnumMap<WeaponClass, SkillLevel>(javaClass<WeaponClass>())

    fun getWeaponProficiency(weaponClass: WeaponClass) =
        weaponSkills[weaponClass]?.proficiency ?: weaponClass.defaultProficiency

    fun exerciseSkill(weaponClass: WeaponClass, target: MessageTarget) {
        if (weaponClass == WeaponClass.NOT_WEAPON)
            return

        val level = weaponSkills.getOrPut(weaponClass) {
            SkillLevel(weaponClass.defaultProficiency)
        }

        level.training++
        val nextLevel = level.proficiency.next
        if (nextLevel != null && level.training >= nextLevel.trainingToReachThisLevel) {
            level.proficiency = nextLevel
            target.message(weaponClass.skillLevelIncreaseMessage)
        }
    }

    fun setWeaponProficiency(weaponClass: WeaponClass, proficiency: Proficiency) {
        val level = weaponSkills.getOrPut(weaponClass) {
            SkillLevel(proficiency)
        }

        level.proficiency = proficiency
        level.training = proficiency.trainingToReachThisLevel
    }

    fun toString() = "[weaponSkills=$weaponSkills]"

    class SkillLevel(var proficiency: Proficiency) {
        var training = 0

        fun toString() = "($proficiency/$training)"
    }
}
