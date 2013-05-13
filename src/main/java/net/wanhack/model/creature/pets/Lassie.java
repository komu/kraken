/*
 *  Copyright 2006 The Wanhack Team
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
import net.wanhack.model.region.Cell;
import net.wanhack.model.region.JumpTarget;

/**
 * AI for Lassie. Lassie is a Pet that returns home immediately.
 */
public class Lassie extends Pet {

    public Lassie(String name) {
        super(name);
        
        setWeight(25);
        setLetter('C');
    }
    
    @Override
    public void talk(Creature talker) {
        talker.message(getName() + " barks.");
    }
    
    @Override
    protected void onTick(Game game) {
        Cell escape = findEscapeStairs();
        
        if (escape != null) {
            if (escape.equals(getCell())) {
                setHitpoints(0);
                removeFromGame();
                game.message("%s went home.", getName());
                
            } else {
                boolean ok = moveTowards(escape);
                if (!ok) {
                    super.onTick(game);
                }
            }
        } else {
            super.onTick(game);
        }
    }
    
    private Cell findEscapeStairs() {
        for (Cell cell : getRegion()) {
            JumpTarget target = cell.getJumpTarget(true);
            if (target != null && target.isExit()) {
                return cell;
            }
        }
        return null;
    }
}
