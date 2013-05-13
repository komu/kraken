/*
 *  Copyright 2005 The Wanhack Team
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.wanhack.model.item.weapon;

import static net.wanhack.model.skill.Proficiency.BASIC;
import static net.wanhack.model.skill.Proficiency.UNSKILLED;
import net.wanhack.model.skill.Proficiency;

public enum WeaponClass {
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

    private final Proficiency defaultProficiency;
    
    private WeaponClass(Proficiency defaultProficiency) {
        this.defaultProficiency = defaultProficiency;
    }
    
    public Proficiency getDefaultProficiency() {
        return defaultProficiency;
    }
    
    @Override
    public String toString() {
        if (this == BLUNT) {
            return "blunt weapon";
        } else if (this == LAUNCHER) {
            return "missile launcher";
        } else {
            return name().toLowerCase();
        }
    }

    public String getSkillLevelIncreaseMessage() {
        if (this == NATURAL) {
            return "You feel more proficient in martial arts.";
        } else {
            return "You feel more proficient in using " + toString() + "s.";   
        }
    }
}
