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

package dev.komu.kraken.model.item.weapon

import dev.komu.kraken.model.skill.Proficiency
import dev.komu.kraken.model.skill.Proficiency.BASIC
import dev.komu.kraken.model.skill.Proficiency.UNSKILLED

enum class WeaponClass(val defaultProficiency: Proficiency) {
    NOT_WEAPON(UNSKILLED),
    NATURAL(BASIC),
    KNIFE(UNSKILLED),
    SWORD(UNSKILLED),
    AXE(UNSKILLED),
    BLUNT(UNSKILLED),
    JAVELIN(UNSKILLED),
    SPEAR(UNSKILLED),
    LAUNCHER(UNSKILLED),
    PROJECTILE(UNSKILLED);

    override fun toString() =
        when(this) {
            BLUNT       -> "blunt weapon"
            LAUNCHER    -> "missile launcher"
            else        -> name.toLowerCase()
        }

    val skillLevelIncreaseMessage: String
        get() =
            if (this == NATURAL)
                "You feel more proficient in martial arts."
            else
                "You feel more proficient in using ${toString()}s."
}
