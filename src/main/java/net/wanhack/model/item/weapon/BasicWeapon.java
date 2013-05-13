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

import net.wanhack.model.creature.Creature;
import net.wanhack.utils.exp.Expression;

/**
 * Useful base class for most of the weapons.
 */
public abstract class BasicWeapon extends Weapon {

    private final WeaponClass weaponClass;
    private String attackVerb = "hit";
    private Expression toHit = Expression.parse("0");
    private Expression damage;
    
    public BasicWeapon(String name, 
                       WeaponClass weaponClass,
                       String damage) {
        super(name);

        setLetter('/');
        setWeight(3);
        this.weaponClass = weaponClass;
        this.damage = Expression.parse(damage);
    }
    
    @Override
    public WeaponClass getWeaponClass() {
        return weaponClass;
    }
    
    @Override
    public String getDescription()
    {
        return buildWeaponDescription(toHit.toString(),
                                      damage.toString());
    }
    
    public void setDamage(Expression damage) {
        this.damage = damage;
    }
    
    public void setToHit(Expression toHit) {
        this.toHit = toHit;
    }

    @Override
    public String getAttackVerb() {
        return attackVerb;
    }
    
    public void setAttackVerb(String attackVerb) {
        this.attackVerb = attackVerb;
    }

    @Override
    public int getToHit(Creature target) {
        return toHit.evaluate();
    }
    
    @Override
    public int getDamage(Creature target) {
        return damage.evaluate();
    }

    private String buildWeaponDescription(String toHit, String damageExpr) {
        String superDesc = super.getDescription();
        
        StringBuilder desc = new StringBuilder("(h: ");
        desc.append(toHit);
        desc.append(", d: ");
        desc.append(damageExpr);
        desc.append("); ");
        desc.append(superDesc);
        return desc.toString();
    }
}
