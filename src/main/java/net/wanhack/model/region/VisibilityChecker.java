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

public class VisibilityChecker {
    
    public CellSet getVisibleCells(Cell from, int sight) {
        CellSet result = new CellSet(from.getRegion());
        
        for (Cell target : from.getRegion()) {
            if (from.distance(target) <= sight) {
                addVisiblePointsTowards(result, from, target);
            }
        }
        
        return result;
    }
    
    private void addVisiblePointsTowards(CellSet result, Cell from, Cell target) {
        for (Cell c : from.getCellsBetween(target)) {
            result.add(c);
            if (!c.canSeeThrough()) {
                return;
            }
        }
        
        result.add(target);
    }
}
