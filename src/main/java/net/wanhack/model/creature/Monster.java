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
package net.wanhack.model.creature;

import net.wanhack.model.Game;
import net.wanhack.model.common.Actor;
import net.wanhack.model.common.Attack;
import net.wanhack.model.item.weapon.NaturalWeapon;
import net.wanhack.model.region.Cell;

public class Monster extends Creature implements Actor {
    
    private Cell lastKnownPlayerPosition = null;
    private Attack naturalWeapon = new NaturalWeapon("hit", "0", "randint(1, 3)");

    public Monster(String name) {
        super(name);
    }
    
    @Override
    protected void onTick(Game game) {
        Player player = game.getPlayer();
        
        boolean seesPlayer = seesCreature(player);
        
        if (seesPlayer) {
            lastKnownPlayerPosition = player.getCell();
        }
        
        if (isFriendly()) {
            moveRandomly();
            return;
        }
        
        if (seesPlayer) {
            if (isAdjacentToCreature(player)) {
                game.attack(this, player);
            } else if (!isImmobile()) {
                moveTowards(player.getCell());
            }
            
        } else {
            if (lastKnownPlayerPosition != null) {
                if (getCell() == lastKnownPlayerPosition) {
                    lastKnownPlayerPosition = null;
                }
            }
            
            if (!isImmobile()) {
                if (lastKnownPlayerPosition != null) {
                    moveTowards(lastKnownPlayerPosition);
                } else {
                    moveRandomly();
                }
            }
        }
    }
    
    @Override
    public Attack getNaturalAttack() {
        return naturalWeapon;
    }
    
    public void setNaturalWeapon(Attack naturalWeapon) {
        this.naturalWeapon = naturalWeapon;
    }
}
