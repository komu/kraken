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
package net.wanhack.model.creature.monsters;

import net.wanhack.model.creature.Monster;
import net.wanhack.model.item.Item;
import net.wanhack.model.item.food.HealingEdible;
import net.wanhack.utils.Probability;
import net.wanhack.utils.RandomUtils;

/**
 * Wraith drops either wraith essence or old rags as corpse. 
 * 
 * @author Tero Poikonen
 */
public class Wraith extends Monster {
    
    public Wraith(String name) {
        super(name);
    }

    @Override
    protected Item getCorpse() {
        if (Probability.check(10)) {
            HealingEdible essence = createItem(HealingEdible.class, "wraith essence");
            essence.setHealingEffect(RandomUtils.rollDie(this.getKillExperience()));
            return essence;
        }
        Item rags = createItem(Item.class, "old rags");
        rags.setColor(this.getColor());
        return rags;
    }
}
