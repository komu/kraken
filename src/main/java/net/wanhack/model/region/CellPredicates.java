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
package net.wanhack.model.region;

import net.wanhack.utils.Predicate;

public class CellPredicates {

    public static final Predicate<Cell> CAN_PUT_CREATURE_ON_CELL = new Predicate<Cell>() {
        public boolean evalute(Cell cell) {
            // For simplicity, we assume corporeal creatures here. This means
            // initial creatures start don't start inside walls even if they could.
            boolean corporeal = true;
            return cell.canMoveInto(corporeal);
        }
    };
    
    public static final Predicate<Cell> FLOOR = new Predicate<Cell>() {
        public boolean evalute(Cell cell) {
            return cell.isFloor();
        }
    };

    public static final Predicate<Cell> IN_ROOM = new Predicate<Cell>() {
        public boolean evalute(Cell cell) {
            return cell.isInRoom();
        }
    };
}
