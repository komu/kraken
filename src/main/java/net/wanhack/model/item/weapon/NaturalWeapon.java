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

import java.io.Serializable;

import net.wanhack.model.common.Attack;
import net.wanhack.model.creature.Creature;
import net.wanhack.utils.exp.Expression;


/**
 * Represents a generic weapon that has a damage roll and a verb.
 * This is not even an item, since it's used to describe the inbuilt
 * attacks of monsters as well.
 */
public class NaturalWeapon implements Attack, Serializable {

    private final String verb;
    private final Expression tohit;
    private final Expression damage;
    protected static final long serialVersionUID = 0;
    
    public NaturalWeapon(String verb, String tohit, String damage) {
        this.verb = verb;
        this.tohit = Expression.parse(tohit);
        this.damage = Expression.parse(damage);
    }
    
    public WeaponClass getWeaponClass() {
        return WeaponClass.NATURAL;
    }

    public String getAttackVerb() {
        return verb;
    }
    
    public int getToHit(Creature target) {
        return tohit.evaluate();
    }
    
    public int getDamage(Creature target) {
        return damage.evaluate();
    }
    
    @Override
    public String toString() {
        return "NaturalWeapon [verb=" + verb + ", tohit=" + tohit + ", damage=" + damage + "]";
    }
}
