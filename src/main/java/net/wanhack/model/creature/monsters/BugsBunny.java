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

import java.awt.Color;

import net.wanhack.model.Game;
import net.wanhack.model.common.Direction;
import net.wanhack.model.creature.Creature;
import net.wanhack.model.creature.Monster;
import net.wanhack.model.creature.Player;
import net.wanhack.utils.RandomUtils;


/**
 * The Bugs Bunny just hops around. From time to time
 * it causes the player to do something random. Here
 * comes bugs with the bunny :)
 * 
 * The bunny is irritably fast. So it is difficult to kill it.
 * 
 * @author Ari Autio
 */
public class BugsBunny extends Monster {
    
    public BugsBunny(String name) {
        super(name);
        
        setLevel(3);
        setHitpoints(1);
        setLetter('r');
        setColor(Color.WHITE);
        setTickRate(40);
    }

    @Override
    public void talk(Creature target) {
        target.say(this, "What's up, Doc?");
    }
    
    @Override
    protected void onTick(Game game) {
        moveRandomly();

        Player player = game.getPlayer();

        if (seesCreature(player)) {
            // calculate new random point for the player
            Direction direction = RandomUtils.randomEnum(Direction.class);
            
            // TODO: movePlayer might cause player to attack, do we want this?
            game.movePlayer(direction);
        }        
    }
}
