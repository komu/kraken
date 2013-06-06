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

package net.wanhack.desktop.game.tile

import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics2D
import java.awt.Paint
import java.lang.Math.max
import java.lang.Math.min
import net.wanhack.model.creature.Creature
import net.wanhack.model.item.Item
import net.wanhack.model.region.Cell
import net.wanhack.model.region.CellType.*
import net.wanhack.model.region.Coordinate

class TileProvider {

    private val font = Font("Monospaced", Font.PLAIN, 14)

    fun getDimensions(width: Int, height: Int) =
        Dimension(width * tileWidth, height * tileHeight)

    val tileWidth = 8
    val tileHeight = 13

    fun drawCell(g: Graphics2D, cell: Cell, visible: Boolean) {
        when (cell.cellType) {
            HALLWAY_FLOOR, ROOM_FLOOR           -> drawFloor(g, cell, visible, false)
            UNDIGGABLE_WALL, ROOM_WALL, WALL    -> drawWall(g, cell.coordinate.x, cell.coordinate.y, visible)
            STAIRS_UP                           -> drawStairs(g, cell, true, visible)
            STAIRS_DOWN                         -> drawStairs(g, cell, false, visible)
            OPEN_DOOR                           -> drawDoor(g, cell, true, visible)
            CLOSED_DOOR                         -> drawDoor(g, cell, false, visible)
            else                                -> { }
        }
    }

    fun drawCreature(g: Graphics2D, cell: Cell, creature: Creature) {
        drawFloor(g, cell, true, true)
        drawLetter(g, cell.coordinate.x, cell.coordinate.y, creature.letter, creature.color.toPaint())
    }
    
    fun drawSelection(g: Graphics2D, coordinate: Coordinate) {
        g.setPaint(Color(0.8.toFloat(), 0.3.toFloat(), 0.3.toFloat(), 0.5.toFloat()))
        g.fillRect(coordinate.x * tileWidth, coordinate.y * tileHeight, tileWidth, tileHeight)
    }

    fun drawItem(g: Graphics2D, cell: Cell, item: Item) {
        drawFloor(g, cell, true, true)
        drawLetter(g, cell.coordinate.x, cell.coordinate.y, item.letter, item.color.toPaint())
    }

    private fun drawLetter(g: Graphics2D, x: Int, y: Int, letter: Char, paint: Paint) {
        g.setFont(font)
        g.setPaint(paint)
        chars[0] = letter
        g.drawChars(chars, 0, 1, x * tileWidth, y * tileHeight + 10)
    }

    private fun drawStairs(g: Graphics2D, cell: Cell, up: Boolean, visible: Boolean) {
        drawFloor(g, cell, visible, false)

        val stairs = if (up) '<' else '>'
        drawLetter(g, cell.coordinate.x, cell.coordinate.y, stairs, Color.BLACK)
    }

    private fun drawDoor(g: Graphics2D, cell: Cell, open: Boolean, visible: Boolean) {
        drawFloor(g, cell, visible, false)

        val letter = if (open) '\'' else '+'
        val paint = if (visible) DOOR_VISIBLE else DOOR_INVISIBLE
        drawLetter(g, cell.coordinate.x, cell.coordinate.y, letter, paint)
    }

    private fun drawFloor(g: Graphics2D, cell: Cell, visible: Boolean, shadow: Boolean) {
        val paint = if (visible) getFloorColor(cell.lighting, shadow) else ROOM_FLOOR_INVISIBLE

        g.setPaint(paint)
        g.fillRect(cell.coordinate.x * tileWidth, cell.coordinate.y * tileHeight, tileWidth, tileHeight)
    }

    private fun getFloorColor(lighting: Int, shadow: Boolean): Paint {
        var d = max(0, min(50 + lighting, 255))
        if (shadow && d > 180)
            d -= 35

        return Color(d, d, d)
    }

    private fun drawWall(g: Graphics2D, x: Int, y: Int, visible: Boolean) {
        g.setPaint(if (visible) WALL_VISIBLE else WALL_INVISIBLE)
        g.fillRect(x * tileWidth, y * tileHeight, tileWidth, tileHeight)
    }

    class object {
        private val chars = CharArray(1)
        private val ROOM_FLOOR_INVISIBLE = Color(80, 80, 80)
        private val WALL_VISIBLE = Color.DARK_GRAY
        private val WALL_INVISIBLE = WALL_VISIBLE.darker()
        private val DOOR_VISIBLE = Color(100, 100, 0)
        private val DOOR_INVISIBLE = DOOR_VISIBLE.darker()
    }

    fun net.wanhack.model.common.Color.toPaint() = Color(r, g, b)
}
