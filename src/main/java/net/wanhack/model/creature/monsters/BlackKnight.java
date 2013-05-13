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
import java.util.List;

import net.wanhack.model.Game;
import net.wanhack.model.common.Actor;
import net.wanhack.model.common.Attack;
import net.wanhack.model.creature.Creature;
import net.wanhack.model.creature.Monster;
import net.wanhack.model.creature.Player;
import net.wanhack.model.item.Item;
import net.wanhack.model.item.weapon.NaturalWeapon;
import net.wanhack.model.item.weapon.Weapon;
import net.wanhack.model.region.Cell;
import net.wanhack.utils.RandomUtils;


/**
 * The Black Knight AI:
 * 
 * Moves towards the player. If the Knight doesn't know where the player is,
 * he just sits and waits.
 * 
 * Attacks always when possible.
 * 
 * Wounding the Black Knight just slows him down. But he will still 
 * continue to attack.
 * 
 * Hitpoint ratio:
 * 
 * >=80% - healthy
 * >=60% - one arm missing
 * >=40% - both arms missing
 * >=20% - both arms and a leg missing
 * <20% - both arms and legs missing... but won't die!
 * 
 * @author Ari Autio
 */
public class BlackKnight extends Monster implements Actor {    

    private static String[] HEALTHY_YELLS = new String[] {
        "None shall pass.", "I move for no man.", "Aaaagh!"  
    };

    private static final String[] ONE_ARMED_YELLS = new String[] {
        "Tis but a scratch.", "I've had worse.", "Come on, you pansy!"
    };

    private static final String[] ARMLESS_YELLS = new String[] {
        "Come on, then.", "Have at you!"
    };

    private static final String[] ONE_LEGGED_YELLS = new String[] {
        "Right. I'll do you for that!", "I'm invincible!"
    };
    
    private static String[] TORSO_YELLS = new String[] {
        "Oh. Oh, I see. Running away, eh?", "You yellow bastard!",
        "Come back here and take what's coming to you.", "I'll bite your legs off!"
    };

    private static String[] PLAYER_FLEEING_YELLS = new String[] {
        "Oh, had enough, eh?", "Just a flesh wound.", "Chicken! Chickennn!"
    };
    
    private Cell lastKnownPlayerPosition = null;
    private final Attack bite = 
        new NaturalWeapon("bite", "1", "randint(0, 1)");
    private boolean hasBeenFighting = false;
    private int maxHitpoints = 1;
    
    public BlackKnight(String name) {
        super(name);

        setLevel(3);
        setTickRate(50); // Black Knight is fast at start
        setLetter('p');
        setColor(Color.BLACK);
        setHitpoints(5);
        setArmorClass(7);
    }
    
    @Override
    public void setHitpoints(int hitpoints) {
        super.setHitpoints(hitpoints);
        
        maxHitpoints = Math.max(maxHitpoints, hitpoints);
    }
    
    @Override
    public void talk(Creature target) {
        int percentage = getHitpointPercentage();
        if (percentage >= 80) {
            target.say(this, RandomUtils.randomItem(HEALTHY_YELLS));
        } else if (percentage >= 60) {
            target.say(this, RandomUtils.randomItem(ONE_ARMED_YELLS));
        } else if (percentage >= 40) {
            target.say(this, RandomUtils.randomItem(ARMLESS_YELLS));
        } else if (percentage >= 20) {
            target.say(this, RandomUtils.randomItem(ONE_LEGGED_YELLS));
        } else {
            target.say(this, RandomUtils.randomItem(TORSO_YELLS));
        }
    }
    
    private int getHitpointPercentage() {
        return 100 * getHitpoints() / maxHitpoints;
    }
    
    @Override
    protected void onTick(Game game) {
        Player player = game.getPlayer();

        boolean isAdjacent = isAdjacentToCreature(player); 
        
        if (hasBeenFighting && !isAdjacent) {
            player.say(this, RandomUtils.randomItem(PLAYER_FLEEING_YELLS));
            hasBeenFighting = false;
        } else if (isAdjacent) {
            talk(player);
        }
        
        if (isAdjacent) {
            game.attack(this, player);
            hasBeenFighting = true;
        } else if (!isFullyCrippled()) {
            if (seesCreature(player)) {
                lastKnownPlayerPosition = player.getCell();
                moveTowards(player.getCell());
            } else {
                if (lastKnownPlayerPosition != null) {
                    if (getCell() == lastKnownPlayerPosition) {
                        lastKnownPlayerPosition = null;
                    }
                }
                
                if (lastKnownPlayerPosition != null) {
                    moveTowards(lastKnownPlayerPosition);
                }
            }
        }
    }
    
    private boolean isFullyCrippled() {
        return getHitpointPercentage() < 20;
    }
   
    @Override
    public Attack getNaturalAttack() {
        return bite;
    }
    
    @Override
    public void takeDamage(int points, Creature attacker) {        
        hasBeenFighting = true;

        setHitpoints(Math.max(1, getHitpoints() - points));
        
        if (isFullyCrippled()) {
            return;
        }

        setTickRate(getTickRate() * 2);
        
        if (isFullyCrippled()) {
            attacker.message("The Black Knight is crippled!");
            Weapon weapon = getWieldedWeapon();
            if (weapon != null) {
                setWieldedWeapon(null);
                dropToAdjacentCell(weapon);
            }
        } else {
            attacker.message("The Black Knight loses a limb.");
        }
    }

    private void dropToAdjacentCell(Item item) {
        List<Cell> cells = getCell().getAdjacentCells();
        RandomUtils.shuffle(cells);
        
        for (Cell cell : cells) {
            if (cell.canDropItemToCell()) {
                cell.addItem(item);
                return;
            }
        }
        
        // No empty adjacent cell found, drop to current cell
        getCell().addItem(item);
    }
}
