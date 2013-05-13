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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents the armoring that a creature has on.
 */
public final class Armoring implements Serializable, Iterable<Armor> {

    private final Map<BodyPart, Armor> armors =
        new EnumMap<BodyPart, Armor>(BodyPart.class);
    private static final long serialVersionUID = 0;

    public Iterator<Armor> iterator() {
        return armors.values().iterator();
    }
    
    public Collection<? extends Armor> removeAllArmors() {
        List<Armor> result = new ArrayList<Armor>(armors.values());
        armors.clear();
        return result;
    }
    
    public int getTotalArmorBonus() {
        int bonus = 0;
        
        for (Armor armor : armors.values()) {
            bonus += armor.getArmorBonus();
        }
        
        return bonus;
    }
    
    public boolean hasArmor(BodyPart part) {
        return armors.containsKey(part);
    }
    
    public Armor getArmor(BodyPart part) {
        return armors.get(part);
    }
    
    public Armor replaceArmor(Armor armor) {
        return armors.put(armor.getBodyPart(), armor);
    }
    
    @Override
    public String toString() {
        return armors.values().toString();
    }
}
