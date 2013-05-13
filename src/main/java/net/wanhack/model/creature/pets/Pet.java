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
package net.wanhack.model.creature.pets;

import net.wanhack.model.Game;
import net.wanhack.model.common.Attack;
import net.wanhack.model.creature.Creature;
import net.wanhack.model.creature.Player;
import net.wanhack.model.item.weapon.NaturalWeapon;
import net.wanhack.model.region.Cell;
import net.wanhack.utils.Probability;

/**
 * Base class for Pets. Pets are friendly towards player and
 * follow him around.
 */
public class Pet extends Creature {

    private Cell lastKnownPlayerPosition = null;
    private Attack naturalWeapon = new NaturalWeapon("bite", "1", "randint(3, 7)");
    
    public Pet(String name) {
        super(name);
        
        setFriendly(true);
    }
    
    @Override
    protected void onTick(Game game) {
        Player player = game.getPlayer();
        
        for (Creature creature : getAdjacentCreatures()) {
            if (!creature.isPlayer()) {
                game.attack(this, creature);
                return;
            }
        }
        
        if (seesCreature(player)) {
            lastKnownPlayerPosition = player.getCell();
            if (isAdjacentToCreature(player) || Probability.check(50)) {
                moveRandomly();
            } else {
                moveTowards(player.getCell());
            }
            
        } else {
            if (lastKnownPlayerPosition != null) {
                if (getCell() == lastKnownPlayerPosition) {
                    lastKnownPlayerPosition = null;
                }
            }
            
            if (lastKnownPlayerPosition != null) {
                moveTowards(lastKnownPlayerPosition);
            } else {
                moveRandomly();
            }
        }
    }
    
    @Override
    protected Attack getNaturalAttack() {
        return naturalWeapon;
    }
}
