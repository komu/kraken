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
import java.awt.Dimension
import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics
import java.awt.Graphics2D
import java.util.ArrayList
import javax.swing.JComponent
import net.wanhack.model.IGame
import net.wanhack.model.creature.HungerLevel
import net.wanhack.model.creature.Player

class StatisticsView: JComponent() {

    private var line1: Line? = null
    private var line2: Line? = null;

    {
        setBackground(Color.BLACK)
        setBorder(null)
        setFont(Font("Monospaced", Font.PLAIN, 14))
    }

    public override fun getPreferredSize() =
        Dimension(300, 2 * (getFontMetrics(getFont())?.getHeight())!!)

    public override fun paint(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setPaint(getBackground())
        g2.fillRect(0, 0, getWidth(), getHeight())
        g2.setFont(getFont())
        val fm = g2.getFontMetrics()!!
        val y1 = fm.getAscent()
        val y2 = fm.getAscent() * 2
        drawLine(g2, line1, y1)
        drawLine(g2, line2, y2)
    }

    private fun drawLine(g: Graphics2D, line: Line?, y: Int) {
        if (line == null)
            return

        val fm = g.getFontMetrics()!!
        var x = 0
        for (fragment in line.fragments) {
            g.setColor(fragment.color)
            g.drawString(fragment.text, x, y)
            x += fm.stringWidth(fragment.text)
        }
    }

    fun updateStatistics(game: IGame?) {
        line1 = getStatsLine1(game)
        line2 = getStatsLine2(game)
        repaint()
    }

    private fun getStatsLine1(game: IGame?): Line? {
        if (game == null)
            return null

        val player = game.player
        val line = Line()
        line.add("%-20s  St:%d Ch:%d", player.name, player.strength, player.charisma)
        return line
    }

    private fun getStatsLine2(game: IGame?): Line? {
        if (game == null)
            return null

        val player = game.player
        val line = Line()
        line.add("%-20s  ", player.region!!.name)
        line.add(getHitPointsColor(player), "HP:%d(%d)", player.hitPoints, player.maximumHitPoints)
        line.add("  AC:%d  Exp:%s(%s)  T:%d    ", player.armorClass, player.level, player.experience, game.getTime())
        line.add(getHungerColor(player.getHungerLevel()), "%-10s", player.getHungerLevel())
        return line
    }

    class object {
        private fun getHitPointsColor(player: Player) =
            if (player.hitPoints <= player.maximumHitPoints / 4)
                Color.RED
            else
                Color.WHITE

        private fun getHungerColor(level: HungerLevel) =
            if (level.hungry) Color.RED else Color.WHITE

        private class Line {
            val fragments = ArrayList<TextFragment>()

            fun add(format: String, vararg args: Any?) {
                fragments.add(TextFragment(format.format(*args), Color.WHITE))
            }

            fun add(color: Color, format: String, vararg args: Any?) {
                fragments.add(TextFragment(format.format(*args), color))
            }
        }

        private class TextFragment(val text: String, val color: Color)
    }
}
