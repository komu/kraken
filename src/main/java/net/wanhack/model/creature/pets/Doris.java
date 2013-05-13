/*
 *  Copyright 2005-2006 The Wanhack Team
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
package net.wanhack.model.creature.pets;

import net.wanhack.model.Game;
import net.wanhack.model.creature.Creature;
import net.wanhack.model.creature.Player;
import net.wanhack.utils.Probability;
import net.wanhack.utils.RandomUtils;

/**
 * AI for Doris the cat. Doris is a normal pet but sometimes attacks
 * the player.
 */
public class Doris extends Pet {

    public Doris(String name) {
        super(name);
        
        setWeight(10);
        setLetter('f');
    }
    
    @Override
    public void talk(Creature talker) {
        String verb = RandomUtils.randomItem("meows", "purrs");
        talker.message("%s %s.", getName(), verb);
    }
    
    @Override
    protected void onTick(Game game) {
        Player player = game.getPlayer();
        
        if (isAdjacentToCreature(player) && Probability.check(1)) {
            game.attack(this, player);
        } else {
            super.onTick(game);
        }
    }
}
