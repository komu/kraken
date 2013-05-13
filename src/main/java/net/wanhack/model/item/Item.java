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
package net.wanhack.model.item;

import java.awt.Color;
import java.io.Serializable;

import net.wanhack.model.common.Attack;
import net.wanhack.model.creature.Creature;
import net.wanhack.model.item.weapon.WeaponClass;
import net.wanhack.utils.RandomUtils;

public class Item implements Attack, Serializable {

    private String unidentifiedTitle;
    private String identifiedTitle;
    private char letter = '*';
    private int weight = 1;
    private int level = 1;
    private Color color = Color.BLACK;
    private boolean identified = false;
    protected static final long serialVersionUID = 0;
    
    public Item(String title) {
        this.unidentifiedTitle = title;
        this.identifiedTitle = title;
    }
    
    public final String getTitle() {
        return identified ? identifiedTitle : unidentifiedTitle;
    }
    
    public String getDescription() {
        return "weight=" + this.getWeight();
    }
    
    /**
     * Returns the effectiveness of this item as light, or 0.
     */
    public int getLighting() {
        return 0;
    }
    
    public final char getLetter() {
        return letter;
    }
    
    public final void setLetter(char letter) {
        this.letter = letter;
    }

    public final Color getColor() {
        return color;
    }
    
    public final void setColor(Color color) {
        this.color = color;
    }
    
    public final int getWeight() {
        return weight;
    }
    
    public final void setWeight(int weight) {
        this.weight = weight;
    }
    
    public final int getLevel() {
        return level;
    }
    
    public final void setLevel(int level) {
        this.level = level;
    }
    
    public final boolean isIdentified() {
        return identified;
    }
    
    public final void setIdentifiedTitle(String identifiedTitle) {
        this.identifiedTitle = identifiedTitle;
    }
    
    public final void setUnidentifiedTitle(String unidentifiedTitle) {
        this.unidentifiedTitle = unidentifiedTitle;
    }
    
    @Override
    public String toString() {
        return getTitle();
    }
    
    public String getAttackVerb() {
        return "hit";
    }
    
    public WeaponClass getWeaponClass() {
        return WeaponClass.NOT_WEAPON;
    }

    public int getToHit(Creature target) {
        return -2; // It's hard to hit with something that's not a weapon.
    }
    
    public int getDamage(Creature target) {
        // non-weapons don't do that much damage
        return (weight > 5000) ? RandomUtils.rollDie(3) :
               (weight > 1000) ? RandomUtils.rollDie(2) :
               1;
    }
}    
