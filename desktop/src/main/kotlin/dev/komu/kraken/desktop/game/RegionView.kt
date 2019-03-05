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

package dev.komu.kraken.desktop.game

import dev.komu.kraken.desktop.game.tile.TileProvider
import dev.komu.kraken.model.GameFacade
import dev.komu.kraken.model.region.*
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.AffineTransform
import javax.swing.JComponent

class RegionView: JComponent() {

    var gameFacade: GameFacade? = null
        get() = field
        set(gameFacade) {
            field = gameFacade
            repaint()
        }

    var translate = true
    private val tileProvider = TileProvider()
    private var transform: AffineTransform? = null

    init {
        background = Color.BLACK
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                gameFacade?.runTowards(toCoordinate(e.point))
            }
        })
    }

    private fun toCoordinate(p: Point):Coordinate {
        val p2 = Point()
        transform?.inverseTransform(p, p2)
        return Coordinate(p2.x / tileProvider.tileWidth, p2.y / tileProvider.tileHeight)
    }


    override fun paint(g: Graphics) {
        val g2 = g as Graphics2D

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF)
        paintBackground(g2)

        gameFacade?.query { game ->
            val region = game.currentRegionOrNull
            if (region != null) {
                transform = getTransform(game.cellInFocus, region.size)
                if (transform != null)
                    g2.transform(transform)

                for (cell in region)
                    paintCell(g2, cell, game.visibleCells)

                val selectedCell = game.selectedCell
                if (selectedCell != null)
                    tileProvider.drawSelection(g2, selectedCell)
            }
        }
    }

    private fun getTransform(coordinate: Coordinate, regionSize: Size): AffineTransform? {
        val width = this.width
        val height = this.height
        val regionWidth = regionSize.width
        val regionHeight = regionSize.width
        val tileWidth = tileProvider.tileWidth
        val tileHeight = tileProvider.tileHeight
        val requiredWidth = tileWidth * regionWidth
        val requiredHeight = tileHeight * regionHeight

        if (width >= requiredWidth && height >= requiredHeight)
            return null

        if (translate) {
            val x = tileWidth * coordinate.x
            val y = tileHeight * coordinate.y
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
        g2.paint = background
        g2.fillRect(0, 0, width, height)
    }

    private fun paintCell(g2: Graphics2D, cell: Cell, visibleCells: CellSet) {
        if (cell in visibleCells) {
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
