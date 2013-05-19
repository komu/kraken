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

package net.wanhack.ui.game

import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.AffineTransform
import javax.swing.JComponent
import net.wanhack.model.GameRef
import net.wanhack.model.creature.Player
import net.wanhack.model.region.Cell
import net.wanhack.model.region.Region
import net.wanhack.ui.game.tile.TileProvider

class RegionView: JComponent() {

    var gameRef: GameRef? = null
        get() = $gameRef
        set(gameRef: GameRef?) {
            $gameRef = gameRef
            repaint()
        }

    var translate = true
    private val tileProvider = TileProvider();

    {
        setBackground(Color.BLACK)
    }

    override fun paint(g: Graphics) {
        val g2 = g as Graphics2D

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF)
        paintBackground(g2)

        gameRef?.executeQuery { game ->
            if (game.player.cellOrNull != null) {
                transformFocusToCell(g2, game.cellInFocus)
                val player = game.player
                for (cell in game.currentRegion)
                    paintCell(g2, cell, player)

                val selectedCell = game.selectedCell
                if (selectedCell != null)
                    tileProvider.drawSelection(g2, selectedCell)
            }
        }
    }

    private fun transformFocusToCell(g2: Graphics2D, cell: Cell) {
        val transform = getTransform(cell)
        if (transform != null)
            g2.transform(transform)
    }

    private fun getTransform(cell: Cell): AffineTransform? {
        val width = getWidth()
        val height = getHeight()
        val regionWidth = cell.region.width
        val regionHeight = cell.region.width
        val tileWidth = tileProvider.tileWidth
        val tileHeight = tileProvider.tileHeight
        val requiredWidth = tileWidth * regionWidth
        val requiredHeight = tileHeight * regionHeight

        if (width >= requiredWidth && height >= requiredHeight)
            return null

        if (translate) {
            val x = tileWidth * cell.x
            val y = tileHeight * cell.y
            val dx = Math.max(0, Math.min(x - width / 2, requiredWidth - width))
            val dy = Math.max(0, Math.min(y - height / 2, requiredHeight - height))
            return AffineTransform.getTranslateInstance((-dx).toDouble(), (-dy).toDouble())
        } else {
            val heightRatio = height.toFloat() / requiredHeight
            val widthRatio = width.toFloat() / requiredWidth
            val scale = Math.min(widthRatio, heightRatio)
            return AffineTransform.getScaleInstance(scale.toDouble(), scale.toDouble())
        }
    }

    private fun paintBackground(g2: Graphics2D) {
        g2.setPaint(getBackground())
        g2.fillRect(0, 0, getWidth(), getHeight())
    }

    private fun paintCell(g2: Graphics2D, cell: Cell, player: Player) {
        if (player.canSee(cell)) {
            val creature = cell.creature
            if (creature != null) {
                tileProvider.drawCreature(g2, cell, creature)
            } else {
                val item = cell.largestItem
                if (item != null)
                    tileProvider.drawItem(g2, cell, item)
                else
                    tileProvider.drawCell(g2, cell, true)
            }
        } else if (cell.hasBeenSeen)
            tileProvider.drawCell(g2, cell, false)
    }

    override fun getPreferredSize() =
        tileProvider.getDimensions(Region.DEFAULT_REGION_WIDTH, Region.DEFAULT_REGION_HEIGHT)
}
