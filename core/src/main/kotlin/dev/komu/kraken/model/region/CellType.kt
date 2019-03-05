/*
 * Copyright 2013 The Releasers of Kraken
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

package dev.komu.kraken.model.region

enum class CellType(val passable: Boolean) {
    HALLWAY_FLOOR(true),
    ROOM_FLOOR(true),
    STAIRS_UP(true),
    STAIRS_DOWN(true),
    WALL(false),
    ROOM_WALL(false),
    UNDIGGABLE_WALL(false),
    OPEN_DOOR (true),
    CLOSED_DOOR(true);

    val isFloor: Boolean
        get() = this == HALLWAY_FLOOR || this == ROOM_FLOOR

    val isDoor: Boolean
        get() = this == OPEN_DOOR || this == CLOSED_DOOR

    val isStairs: Boolean
        get() = this == STAIRS_DOWN || this == STAIRS_UP

    val isRoomFloor: Boolean
        get() = this == ROOM_FLOOR || this == STAIRS_UP || this == STAIRS_DOWN

    val canDropItem: Boolean
        get() = isFloor || isStairs || this == OPEN_DOOR

    val canSeeThrough: Boolean
        get() = isFloor || isStairs || this == OPEN_DOOR

    fun canMoveInto(corporeal: Boolean) =
        this != UNDIGGABLE_WALL && (!corporeal || isFloor || isStairs || this == OPEN_DOOR)
}
