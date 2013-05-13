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
package net.wanhack.model.region;

import net.wanhack.model.creature.Creature;
import net.wanhack.model.creature.Player;
import net.wanhack.utils.Probability;

public class Door implements CellState {

    private enum State { 
        HIDDEN(CellType.ROOM_WALL), 
        OPEN(CellType.OPEN_DOOR), 
        CLOSED(CellType.CLOSED_DOOR);
        
        final CellType type;
        
        State(CellType type) {
            this.type = type;
        }
    }
    
    private State state;
    private static final Probability SEARCH_PROBABILITY = new Probability(10);
    
    public Door(boolean hidden) {
        this.state = hidden ? State.HIDDEN : State.CLOSED;
    }
    
    public boolean search(Player searcher) {
        if (state == State.HIDDEN && SEARCH_PROBABILITY.check()) {
            state = State.CLOSED;
            searcher.message("%s %s a hidden door.",
                             searcher.You(), searcher.verb("find"));
            return true;
        } else {
            return false;
        }
    }
    
    public boolean isOpen() {
        return state == State.OPEN;
    }

    public CellType getType() {
        return state.type;
    }

    void open(Creature opener) {
        if (state == State.CLOSED) {
            if (Probability.check(opener.getStrength())) {
                state = State.OPEN;
                opener.message("Opened door.");
            } else {
                opener.message("The door resists.");
            }
        }
    }

    void close(Creature closer) {
        if (state == State.OPEN) {
            if (Probability.check(closer.getStrength())) {
                state = State.CLOSED;
                closer.message("Closed door.");
            } else {
                closer.message("The door resists.");
            }
        }
    }
}
