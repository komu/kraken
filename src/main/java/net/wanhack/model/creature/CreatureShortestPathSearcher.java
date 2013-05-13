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

import net.wanhack.model.region.Cell;
import net.wanhack.model.region.ShortestPathSearcher;

/**
 * Shortest path searcher for creatures. Takes account the special
 * abilities of creatures (e.g. some creatures can go through walls).
 */
public class CreatureShortestPathSearcher extends ShortestPathSearcher {

    private final Creature creature;
    
    public CreatureShortestPathSearcher(Creature creature) {
        super(creature.getRegion());
        
        this.creature = creature;
    }

    @Override
    protected int costToEnter(Cell cell) {
        if (cell.getCreature() != null) {
            // By making the cost of populated cell higher, we make sure that
            // creatures try to move past each others, but will still follow
            // the proper path if the only path to target is blocked by some
            // other creature.
            
            // This doesn't check if the creature sees the other: creatures
            // have an instinct that enables them to just know the shortest
            // path even if they can't see it for sure. :)
            
            return 5;
            
        } else if (cell.isClosedDoor()) {
            return creature.getCanUseDoors() ? 2 : 10;
        } else {
            return 1;
        }
    }
    
    @Override
    protected boolean canEnter(Cell cell) {
        if (creature.isCorporeal()) {
            return cell.isPassable();
        } else {
            return true;
        }
    }
}
