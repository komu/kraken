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

import net.wanhack.model.creature.HungerLevel;
import net.wanhack.model.creature.Player;

public class WaferThinMint extends Food {

    public WaferThinMint(String name) {
        super(name);
        
        setWeight(20);
    }

    @Override
    public void onEatenBy(Player eater) {
        if (eater.getHungerLevel() == HungerLevel.SATIATED) {
            eater.setHitpoints(0);
            eater.message("This %s is too much. %s %s!", getTitle(), eater.You(), eater.verb("explode"));
            eater.die(getTitle());
        } else {
            eater.decreaseHungriness(1);
            eater.message("What a delicious %s!", getTitle());
        }
    }
}
