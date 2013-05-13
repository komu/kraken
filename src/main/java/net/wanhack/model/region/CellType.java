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

public enum CellType {
    HALLWAY_FLOOR(true),
    ROOM_FLOOR(true),
    STAIRS_UP(true),
    STAIRS_DOWN(true),
    WALL(false),
    ROOM_WALL(false),
    UNDIGGABLE_WALL(false),
    OPEN_DOOR(true),
    CLOSED_DOOR(true);

    /** 
     * True if this cells having this type can be made passable without
     * too much fuss. Closed doors, e.g. are considered passable, even
     * though they can't be passed without changing them to open doors.
     * Walls, however, are not considered passable even though they can
     * be digged to make the cell passable.
     * <p>
     * Basically passability is not used to make decision if a creature
     * can move to a cell or not, but just to decide if certain cell is
     * somehow reachable from another (e.g. when calculating shortest
     * paths for movement).
     */
    private final boolean passable;
    
    private CellType(boolean passable)  {
        this.passable = passable;
    }
    
    public boolean isPassable() {
        return passable;
    }

    public boolean isFloor() {
        return this == HALLWAY_FLOOR || this == ROOM_FLOOR;
    }
    
    public boolean isDoor() {
        return this == OPEN_DOOR || this == CLOSED_DOOR;
    }

    public boolean isStairs() {
        return this == STAIRS_DOWN || this == STAIRS_UP;
    }
    
    public boolean isRoomFloor() {
        return this == ROOM_FLOOR || this == STAIRS_UP || this == STAIRS_DOWN;
    }

    public boolean canDropItem() {
        return isFloor() || isStairs() || this == OPEN_DOOR;
    }

    public boolean canSeeThrough() {
        return isFloor() || isStairs() || this == OPEN_DOOR;
    }

    public boolean canMoveInto(boolean corporeal) {
        if (!corporeal) return true;
        
        return isFloor() || isStairs() || this == OPEN_DOOR;
    }
}
