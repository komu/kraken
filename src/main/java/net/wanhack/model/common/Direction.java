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
package net.wanhack.model.common;

public enum Direction {
    NORTH("N", 0, -1), NE("NE", 1, -1), 
    EAST("E", 1, 0),   SE("SE", 1, 1), 
    SOUTH("S", 0, 1),  SW("SW", -1, 1), 
    WEST("W", -1, 0),  NW("NW", -1, -1);
    
    private static Direction[] MAIN_DIRECTIONS = { NORTH, EAST, SOUTH, WEST };
    
    public final int dx;
    public final int dy;
    private final String shortName;

    public static Direction[] getMainDirections() {
        return MAIN_DIRECTIONS;
    }

    public boolean isOpposite(Direction rhs) {
        return dx == -rhs.dx && dy == -rhs.dy;
    }
    
    private Direction(String shortName, int dx, int dy) {
        this.shortName = shortName;
        this.dx = dx;
        this.dy = dy;
    }

    public String getShortName() {
        return shortName;
    }
}
