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
import net.wanhack.utils.RandomUtils;
import net.wanhack.utils.exp.Expression;

public class Corpse extends Food {
    
    private Expression poisonDamage;
    private Taste taste = Taste.CHICKEN;
    
    public Corpse(String name) {
        super(name);
    }
    
    @Override
    public void onEatenBy(Player eater) {
        eater.decreaseHungriness(getEffectiveness());
        int poisonDamage = calculatePoisonDamage();
        if (poisonDamage > 0) {
            eater.takeDamage(poisonDamage, eater);
            eater.message("This %s tastes terrible, it must have been poisonous!", 
                            getTitle());
            if (!eater.isAlive()) {
                eater.message("%s %s.", eater.You(), eater.verb("die"));
                eater.die("poisonous corpse");
            }
        } else {
            eater.message("This %s tastes %s.", getTitle(), taste);
        }
    }
    
    public void setPoisonDamage(Expression poisonDamage) {
        this.poisonDamage = poisonDamage;
    }
    
    public void setTaste(Taste taste) {
        this.taste = taste;
    }
    
    private int calculatePoisonDamage() {
        if (poisonDamage == null) return 0;
        
        int baseDamage = poisonDamage.evaluate();
        if (baseDamage > 0) {
            return RandomUtils.rollDie(baseDamage * this.getLevel());
        } else {
            return 0;
        }
    }
}
