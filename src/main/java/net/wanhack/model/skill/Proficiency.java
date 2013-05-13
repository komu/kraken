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
package net.wanhack.model.skill;

import static net.wanhack.utils.NumberUtils.square;

/**
 * Represents the proficiency in a skill.
 */
public enum Proficiency {

    UNSKILLED(1, -2),
    BASIC(2, 0),
    SKILLED(3, 1),
    EXPERT(4, 2),
    MASTER(5, 3),
    GRAND_MASTER(6, 5);
    
    private final int level;
    private final int bonus;
    
    private Proficiency(int level, int bonus) {
        this.level = level;
        this.bonus = bonus;
    }
    
    public int getBonus() {
        return bonus;
    }

    public Proficiency next() {
        if (ordinal() + 1 < values().length) {
            return Proficiency.values()[ordinal() + 1];
        } else {
            return null;
        }
    }
    
    int getTrainingToReachThisLevel() {
        return square(level - 1) * 20;
    }
    
    @Override
    public String toString() {
        if (this != GRAND_MASTER) {
            return name().toLowerCase();
        } else {
            return "grand master";
        }
    }
}
