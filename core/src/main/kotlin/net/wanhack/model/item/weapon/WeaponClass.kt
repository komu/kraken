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

import net.wanhack.model.skill.Proficiency
import net.wanhack.model.skill.Proficiency.BASIC
import net.wanhack.model.skill.Proficiency.UNSKILLED

enum class WeaponClass(val defaultProficiency: Proficiency) {
    NOT_WEAPON : WeaponClass(UNSKILLED)
    NATURAL : WeaponClass(BASIC)
    KNIFE : WeaponClass(UNSKILLED)
    SWORD : WeaponClass(UNSKILLED)
    AXE : WeaponClass(UNSKILLED)
    BLUNT : WeaponClass(UNSKILLED)
    JAVELIN : WeaponClass(UNSKILLED)
    SPEAR : WeaponClass(UNSKILLED)
    LAUNCHER : WeaponClass(UNSKILLED)
    PROJECTILE : WeaponClass(UNSKILLED)

    fun toString() =
        when(this) {
            BLUNT       -> "blunt weapon"
            LAUNCHER    -> "missile launcher"
            else        -> name().toLowerCase()
        }

    val skillLevelIncreaseMessage: String
        get() =
            if (this == NATURAL)
                "You feel more proficient in martial arts."
            else
                "You feel more proficient in using ${toString()}s."
}
