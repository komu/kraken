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

package net.wanhack.model

import java.io.ObjectOutputStream
import net.wanhack.model.common.Direction
import net.wanhack.model.creature.Player
import net.wanhack.model.item.Item
import net.wanhack.model.region.Cell
import net.wanhack.model.region.Region

trait IGame {
    fun start()
    fun talk()
    fun openDoor()
    fun closeDoor()
    fun pickup()
    fun wield()
    fun wear()
    fun drop()
    fun drop(item: Item)
    fun eat()
    fun fling()
    fun search()
    fun movePlayer(direction: Direction)
    fun runTowards(direction: Direction)
    fun movePlayerVertically(up: Boolean)
    fun skipTurn()
    fun rest(maxTurns: Int)
    fun revealCurrentRegion()

    val currentRegion: Region
    val player: Player

    val score: Int
    val time: Int
    val dungeonLevel: Int
    val maxDungeonLevel: Int
    val cellInFocus: Cell
    val selectedCell: Cell?
    fun save(oos: ObjectOutputStream?)
}
