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
package net.wanhack.model.item.armor;

import net.wanhack.model.item.Item;

public class Armor extends Item {

    private int armorBonus = 1;
    private BodyPart bodyPart = BodyPart.TORSO;
    
    public Armor(String name) {
        super(name);
    }
    
    public int getArmorBonus() {
        return armorBonus;
    }
    
    public void setArmorBonus(int armorBonus) {
        this.armorBonus = armorBonus;
    }

    public BodyPart getBodyPart() {
        return bodyPart;
    }
    
    public void setBodyPart(BodyPart bodyPart) {
        this.bodyPart = bodyPart;
    }

    @Override
    public String getDescription()
    {
        return  "ac: " + this.armorBonus + "; " + super.getDescription();
    }

}

