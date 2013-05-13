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

package net.wanhack.ui.game.tile

import java.awt.Dimension
import java.awt.Graphics2D
import net.wanhack.model.creature.Creature
import net.wanhack.model.item.Item
import net.wanhack.model.region.Cell

trait TileProvider {
    fun drawCell(g: Graphics2D, cell: Cell, visible: Boolean)
    fun drawCreature(g: Graphics2D, cell: Cell, creature: Creature)
    fun drawItem(g: Graphics2D, cell: Cell, item: Item)
    fun drawSelection(g: Graphics2D, cell: Cell)
    fun getDimensions(width: Int, height: Int): Dimension
    val tileWidth: Int
    val tileHeight: Int
}
