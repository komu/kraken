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
package net.wanhack.model.item.food;

import net.wanhack.model.creature.Player;
import net.wanhack.model.item.Item;

public class Food extends Item {

    private int effectiveness = 100;
    
    public Food(String name) {
        super(name);
        
        setLetter('%');
    }
    
    public void setEffectiveness(int effectiveness) {
        this.effectiveness = effectiveness;
    }
    
    public int getEffectiveness() {
        return effectiveness;
    }
    
    public void onEatenBy(Player eater) {
        eater.decreaseHungriness(effectiveness);
        eater.message("This %s is delicious!", getTitle());
    }
}
