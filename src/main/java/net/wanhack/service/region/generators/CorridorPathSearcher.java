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
package net.wanhack.service.region.generators;

import net.wanhack.model.region.Cell;
import net.wanhack.model.region.CellType;
import net.wanhack.model.region.Region;
import net.wanhack.model.region.ShortestPathSearcher;

final class CorridorPathSearcher extends ShortestPathSearcher {

    public CorridorPathSearcher(Region region) {
        super(region);
        
        setAllowSubdirections(false);
    }
    
    @Override
    protected boolean canEnter(Cell cell) {
        return cell.getType() != CellType.UNDIGGABLE_WALL;
    }
    
    @Override
    protected int costToEnter(Cell cell) {
        switch (cell.getType()) {
        case UNDIGGABLE_WALL:   return 100000;
        case ROOM_FLOOR:        return 100;
        case HALLWAY_FLOOR:     return 50;
        case ROOM_WALL:         return 200;
        case WALL:              return 100;
        default:                return 500;
        }
    }
}
