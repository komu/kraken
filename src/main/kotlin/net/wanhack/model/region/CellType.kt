/*
 * Copyright 2013 The Wanhack Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.wanhack.model.region

enum class CellType(val passable: Boolean) {
    HALLWAY_FLOOR : CellType(true)
    ROOM_FLOOR : CellType(true)
    STAIRS_UP : CellType(true)
    STAIRS_DOWN : CellType(true)
    WALL : CellType(false)
    ROOM_WALL : CellType(false)
    UNDIGGABLE_WALL : CellType(false)
    OPEN_DOOR : CellType(true)
    CLOSED_DOOR : CellType(true)

    fun isPassable(): Boolean {
        return passable
    }

    fun isFloor(): Boolean {
        return this == HALLWAY_FLOOR || this == ROOM_FLOOR
    }

    fun isDoor(): Boolean {
        return this == OPEN_DOOR || this == CLOSED_DOOR
    }

    fun isStairs(): Boolean {
        return this == STAIRS_DOWN || this == STAIRS_UP
    }

    fun isRoomFloor(): Boolean {
        return this == ROOM_FLOOR || this == STAIRS_UP || this == STAIRS_DOWN
    }

    fun canDropItem(): Boolean {
        return isFloor() || isStairs() || this == OPEN_DOOR
    }

    fun canSeeThrough(): Boolean {
        return isFloor() || isStairs() || this == OPEN_DOOR
    }

    fun canMoveInto(corporeal: Boolean): Boolean {
        if (!corporeal)
            return true

        return isFloor() || isStairs() || this == OPEN_DOOR
    }
}
