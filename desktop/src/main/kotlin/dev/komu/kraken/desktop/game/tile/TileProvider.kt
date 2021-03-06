package dev.komu.kraken.desktop.game.tile

import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.item.Item
import dev.komu.kraken.model.region.Cell
import dev.komu.kraken.model.region.CellType.*
import java.awt.*
import java.lang.Math.max
import java.lang.Math.min

class TileProvider {

    private val font = Font("Monospaced", Font.PLAIN, 14)

    fun getDimensions(width: Int, height: Int) =
        Dimension(width * tileWidth, height * tileHeight)

    val tileWidth = 8
    val tileHeight = 13

    fun drawCell(g: Graphics2D, cell: Cell, visible: Boolean) {
        when (cell.type) {
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
        drawFloor(g, cell, true, shadow = true)
        drawLetter(g, cell.coordinate.x, cell.coordinate.y, creature.letter, creature.color.toPaint())
    }
    
    fun drawItem(g: Graphics2D, cell: Cell, item: Item) {
        drawFloor(g, cell, true, shadow = true)
        drawLetter(g, cell.coordinate.x, cell.coordinate.y, item.letter, item.color.toPaint())
    }

    private fun drawLetter(g: Graphics2D, x: Int, y: Int, letter: Char, paint: Paint) {
        g.font = font
        g.paint = paint
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

        g.paint = paint
        g.fillRect(cell.coordinate.x * tileWidth, cell.coordinate.y * tileHeight, tileWidth, tileHeight)
    }

    private fun getFloorColor(lighting: Int, shadow: Boolean): Paint {
        var d = max(0, min(50 + lighting, 255))
        if (shadow && d > 180)
            d -= 35

        return Color(d, d, d)
    }

    private fun drawWall(g: Graphics2D, x: Int, y: Int, visible: Boolean) {
        g.paint = if (visible) WALL_VISIBLE else WALL_INVISIBLE
        g.fillRect(x * tileWidth, y * tileHeight, tileWidth, tileHeight)
    }

    companion object {
        private val chars = CharArray(1)
        private val ROOM_FLOOR_INVISIBLE = Color(80, 80, 80)
        private val WALL_VISIBLE = Color.DARK_GRAY
        private val WALL_INVISIBLE = WALL_VISIBLE.darker()
        private val DOOR_VISIBLE = Color(100, 100, 0)
        private val DOOR_INVISIBLE = DOOR_VISIBLE.darker()
    }

    private fun dev.komu.kraken.model.common.Color.toPaint() = Color(r, g, b)
}
