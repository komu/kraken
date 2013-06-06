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

import android.content.Context
import android.view.View
import android.util.AttributeSet
import android.graphics.Canvas
import net.wanhack.model.GameFacade
import net.wanhack.model.region.Cell
import net.wanhack.model.creature.Player
import android.graphics.Matrix
import net.wanhack.model.region.Region
import net.wanhack.model.region.Coordinate
import net.wanhack.model.region.Size

class GameView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    var game: GameFacade? = null
    var translate = true
    val tileProvider = TileProvider()

    protected override fun onDraw(canvas: Canvas?) {
        canvas!!

        game?.query { game ->
            val player = game.player
            if (player.cellOrNull != null) {
                transformFocusToCell(canvas, game.cellInFocus, game.currentRegion.size)
                for (cell in game.currentRegion)
                    paintCell(canvas, cell, player)
            }
        }
    }

    fun paintCell(canvas: Canvas, cell: Cell, player: Player) {
        if (player.canSee(cell)) {
            val creature = cell.creature
            if (creature != null) {
                tileProvider.drawCreature(canvas, cell, creature)
            } else {
                val item = cell.largestItem
                if (item != null)
                    tileProvider.drawItem(canvas, cell, item)
                else
                    tileProvider.drawCell(canvas, cell, true)
            }
        } else if (cell.hasBeenSeen)
            tileProvider.drawCell(canvas, cell, false)
    }

    fun transformFocusToCell(canvas: Canvas, coordinate: Coordinate, regionSize: Size) {
        val matrix = getTransform(coordinate, regionSize)
        if (matrix != null)
            canvas.setMatrix(matrix)
    }

    private fun getTransform(coordinate: Coordinate, regionSize: Size): Matrix? {
        val width = getWidth()
        val height = getHeight()
        val tileWidth = tileProvider.tileWidth
        val tileHeight = tileProvider.tileHeight
        val requiredWidth = tileWidth * regionSize.width
        val requiredHeight = tileHeight * regionSize.height

        if (width >= requiredWidth && height >= requiredHeight)
            return null

        val matrix = Matrix()
        if (translate) {
            val x = tileWidth * coordinate.x
            val y = tileHeight * coordinate.y
            val dx = Math.max(0, Math.min(x - width / 2, requiredWidth - width))
            val dy = Math.max(0, Math.min(y - height / 2, requiredHeight - height))

            matrix.setTranslate(-dx.toFloat(), -dy.toFloat())
        } else {
            val heightRatio = height.toFloat() / requiredHeight
            val widthRatio = width.toFloat() / requiredWidth
            val scale = Math.min(widthRatio, heightRatio)

            matrix.setScale(scale, scale)
        }
        return matrix;
    }

}
