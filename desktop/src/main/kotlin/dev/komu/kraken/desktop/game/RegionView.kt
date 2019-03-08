package dev.komu.kraken.desktop.game

import dev.komu.kraken.desktop.game.tile.TileProvider
import dev.komu.kraken.model.GameFacade
import dev.komu.kraken.model.region.*
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.AffineTransform
import javax.swing.JComponent
import kotlin.math.max
import kotlin.math.min

class RegionView: JComponent() {

    var gameFacade: GameFacade? = null
        set(gameFacade) {
            field = gameFacade
            repaint()
        }

    var translate = true
    private val tileProvider = TileProvider()

    init {
        background = Color.BLACK
    }

    override fun paint(g: Graphics) {
        val g2 = g as Graphics2D

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF)
        paintBackground(g2)

        gameFacade?.query { game ->
            val region = game.currentRegionOrNull
            if (region != null) {
                val transform = getTransform(game.cellInFocus, region.size)
                if (transform != null)
                    g2.transform(transform)

                for (cell in region)
                    paintCell(g2, cell, game.visibleCells)
            }
        }
    }

    private fun getTransform(coordinate: Coordinate, regionSize: Size): AffineTransform? {
        val width = this.width
        val height = this.height
        val regionWidth = regionSize.width
        val tileWidth = tileProvider.tileWidth
        val tileHeight = tileProvider.tileHeight
        val requiredWidth = tileWidth * regionWidth
        val requiredHeight = tileHeight * regionSize.width

        if (width >= requiredWidth && height >= requiredHeight)
            return null

        return if (translate) {
            val x = tileWidth * coordinate.x
            val y = tileHeight * coordinate.y
            val dx = max(0, min(x - width / 2, requiredWidth - width))
            val dy = max(0, min(y - height / 2, requiredHeight - height))
            AffineTransform.getTranslateInstance((-dx).toDouble(), (-dy).toDouble())
        } else {
            val heightRatio = height.toDouble() / requiredHeight
            val widthRatio = width.toDouble() / requiredWidth
            val scale = min(widthRatio, heightRatio)
            AffineTransform.getScaleInstance(scale, scale)
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
                    tileProvider.drawCell(g2, cell, visible = true)
            }
        } else if (cell.seen)
            tileProvider.drawCell(g2, cell, visible = false)
    }

    override fun getPreferredSize() =
        tileProvider.getDimensions(Region.DEFAULT_REGION_WIDTH, Region.DEFAULT_REGION_HEIGHT)
}
