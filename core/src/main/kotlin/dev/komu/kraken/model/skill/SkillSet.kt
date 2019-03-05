/*
 * Copyright 2013 The Releasers of Kraken
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

package dev.komu.kraken.model.skill

import dev.komu.kraken.model.common.MessageTarget
import dev.komu.kraken.model.item.weapon.WeaponClass
import java.util.*

class SkillSet {
    private val weaponSkills = EnumMap<WeaponClass, SkillLevel>(WeaponClass::class.java)

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

    override fun toString() = "[weaponSkills=$weaponSkills]"

    class SkillLevel(var proficiency: Proficiency) {
        var training = 0

        override fun toString() = "($proficiency/$training)"
    }
}
