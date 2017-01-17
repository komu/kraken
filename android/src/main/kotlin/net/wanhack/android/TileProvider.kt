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

package net.wanhack.android

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import net.wanhack.model.creature.Creature
import net.wanhack.model.item.Item
import net.wanhack.model.region.Cell
import net.wanhack.model.region.CellType.*
import java.lang.Math.max
import java.lang.Math.min
import net.wanhack.model.common.Color as WColor

class TileProvider {

    val tileWidth = 16
    val tileHeight = 26

    fun drawCell(g: Canvas, cell: Cell, visible: Boolean) {
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

    fun drawCreature(g: Canvas, cell: Cell, creature: Creature) {
        drawFloor(g, cell, true, true)
        drawLetter(g, cell.coordinate.x, cell.coordinate.y, creature.letter, creature.color.toPaint())
    }

    fun drawItem(g: Canvas, cell: Cell, item: Item) {
        drawFloor(g, cell, true, true)
        drawLetter(g, cell.coordinate.x, cell.coordinate.y, item.letter, item.color.toPaint())
    }

    private fun drawLetter(g: Canvas, x: Int, y: Int, letter: Char, paint: Paint) {
        // g.font = font

        val bounds = Rect()
        paint.textSize = 20f

        chars[0] = letter
        paint.getTextBounds(chars, 0, 1, bounds);

        g.drawText(chars, 0, 1, (x * tileWidth).toFloat(), (y * tileHeight - bounds.top).toFloat(), paint)
    }

    private fun drawStairs(g: Canvas, cell: Cell, up: Boolean, visible: Boolean) {
        drawFloor(g, cell, visible, false)

        val stairs = if (up) '<' else '>'
        drawLetter(g, cell.coordinate.x, cell.coordinate.y, stairs, BLACK)
    }

    private fun drawDoor(g: Canvas, cell: Cell, open: Boolean, visible: Boolean) {
        drawFloor(g, cell, visible, false)

        val letter = if (open) '\'' else '+'
        val paint = if (visible) DOOR_VISIBLE else DOOR_INVISIBLE
        drawLetter(g, cell.coordinate.x, cell.coordinate.y, letter, paint)
    }

    private fun drawFloor(g: Canvas, cell: Cell, visible: Boolean, shadow: Boolean) {
        val paint = if (visible) getFloorColor(cell.lighting, shadow) else ROOM_FLOOR_INVISIBLE

        val xx = cell.coordinate.x * tileWidth
        val yy = cell.coordinate.y * tileHeight
        g.drawRect(xx, yy, xx+tileWidth, yy+tileHeight, paint)
    }

    private fun getFloorColor(lighting: Int, shadow: Boolean): Paint {
        var d = max(0, min(50 + lighting, 255))
        if (shadow && d > 180)
            d -= 35

        return WColor(d, d, d).toPaint()
    }

    private fun drawWall(g: Canvas, x: Int, y: Int, visible: Boolean) {
        val paint = if (visible) WALL_VISIBLE else WALL_INVISIBLE

        val xx = x * tileWidth
        val yy = y * tileHeight
        g.drawRect(xx, yy, xx+tileWidth, yy+tileHeight, paint)
    }

    companion object {
        private val chars = CharArray(1)
        private val BLACK = WColor.BLACK.toPaint()
        private val ROOM_FLOOR_INVISIBLE = WColor(80, 80, 80).toPaint()
        private val WALL_VISIBLE = WColor.DARK_GRAY.toPaint()
        private val WALL_INVISIBLE = WColor.DARK_GRAY.darker().toPaint()
        private val DOOR_VISIBLE = WColor(100, 100, 0).toPaint()
        private val DOOR_INVISIBLE = WColor(100, 100, 0).darker().toPaint()

        fun WColor.toPaint() = Paint().apply {
            color = Color.rgb(r, g, b)
        }

        fun Canvas.drawRect(x1: Int, y1: Int, x2: Int, y2: Int, paint: Paint) {
            this.drawRect(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat(), paint)
        }
    }
}
