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

import net.wanhack.model.Game;
import net.wanhack.model.common.Actor;
import net.wanhack.model.creature.Monster;
import net.wanhack.model.creature.Player;
import net.wanhack.utils.RandomUtils;

/**
 * The Knight of Ni is quite a normal creature, but he may say
 * "Ni!" every now and then to decrease sanity.
 *  
 * @author Tero P
 */
public class KnightOfNi extends Monster implements Actor {    

    public KnightOfNi(String name) {
        super(name);
        // attributes defined in xml
    }
    
    @Override
    protected void onTick(Game game) {
        Player player = game.getPlayer();
        
        if (seesCreature(player)) {
            int rand = RandomUtils.rollDie(20);
            if (rand == 3) {
                player.say(this, "Noo!");
            } else if (rand < 3) {
                player.say(this, "Ni!");
                // TODO: decrease sanity by 1
            }
        }
        super.onTick(game);
    }
}
