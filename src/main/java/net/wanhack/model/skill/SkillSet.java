/*
 *  Copyright 2005-2006 The Wanhack Team
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
package net.wanhack.model.skill;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

import net.wanhack.common.MessageTarget;
import net.wanhack.model.item.weapon.WeaponClass;

public final class SkillSet implements Serializable {

    private final Map<WeaponClass, SkillLevel> weaponSkills = 
        new EnumMap<WeaponClass, SkillLevel>(WeaponClass.class);
    private static final long serialVersionUID = 0;
    
    public Proficiency getWeaponProficiency(WeaponClass weaponClass) {
        SkillLevel level = weaponSkills.get(weaponClass);
        if (level != null) {
            return level.proficiency;
        } else {
            return weaponClass.getDefaultProficiency();
        }
    }
    
    public void exerciseSkill(WeaponClass weaponClass, MessageTarget target) {
        if (weaponClass == WeaponClass.NOT_WEAPON) return;
        
        SkillLevel level = weaponSkills.get(weaponClass);
        if (level == null) {
            level = new SkillLevel();
            level.proficiency = weaponClass.getDefaultProficiency();
            weaponSkills.put(weaponClass, level);
        }
        
        level.training++;
        Proficiency nextLevel = level.proficiency.next();
        if (nextLevel != null && level.training >= nextLevel.getTrainingToReachThisLevel()) {
            level.proficiency = nextLevel;
            target.message(weaponClass.getSkillLevelIncreaseMessage());
        }
    }

    public void setWeaponProficiency(WeaponClass weaponClass, Proficiency proficiency) {
        SkillLevel level = weaponSkills.get(weaponClass);
        if (level == null) {
            level = new SkillLevel();
            weaponSkills.put(weaponClass, level);
        }
        
        level.proficiency = proficiency;
        level.training = proficiency.getTrainingToReachThisLevel();
    }
    
    @Override
    public String toString() {
        return "[weaponSkills=" + weaponSkills.toString() + "]";
    }
    
    private static class SkillLevel implements Serializable {
        private Proficiency proficiency;
        private int training = 0;
        private static final long serialVersionUID = 0;
        
        @Override
        public String toString() {
            return "(" + proficiency + "/" + training + ")";
        }
    }
}
