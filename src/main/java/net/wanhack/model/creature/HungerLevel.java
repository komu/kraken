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
package net.wanhack.model.creature;

public enum HungerLevel {
    SATIATED(2001), NOT_HUNGRY(301), HUNGRY(101), WEAK(1), FAINTING(0);
    
    private int min;

    private HungerLevel(int min) {
        this.min = min;
    }

    public boolean isHungry() {
        return min <= HUNGRY.min;
    }
    
    @Override
    public String toString() {
        if (this == NOT_HUNGRY) {
            return "";
        } else {
            return name().toLowerCase();
        }
    }

    public static HungerLevel getHungerLevel(int level) {
        for (HungerLevel hunger : values()) {
            if (level >= hunger.min) {
                return hunger;
            }
        }
        
        return FAINTING;
    }
}
