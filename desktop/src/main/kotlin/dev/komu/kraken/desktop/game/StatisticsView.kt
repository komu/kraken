package dev.komu.kraken.desktop.game

import dev.komu.kraken.model.GameStatistics
import dev.komu.kraken.model.creature.HungerLevel
import java.awt.*
import java.util.*
import javax.swing.JComponent

class StatisticsView: JComponent() {

    private var line1: Line? = null
    private var line2: Line? = null

    init {
        background = Color.BLACK
        border = null
        font = Font("Monospaced", Font.PLAIN, 14)
    }

    override fun getPreferredSize() =
        Dimension(300, 2 * getFontMetrics(font).height)

    override fun paint(g: Graphics) {
        val g2 = g as Graphics2D
        g2.paint = background
        g2.fillRect(0, 0, width, height)
        g2.font = font
        val fm = g2.fontMetrics
        drawLine(g2, line1, fm.ascent)
        drawLine(g2, line2, fm.ascent * 2)
    }

    private fun drawLine(g: Graphics2D, line: Line?, y: Int) {
        if (line == null)
            return

        val fm = g.fontMetrics
        var x = 0
        for (fragment in line.fragments) {
            g.color = fragment.color
            g.drawString(fragment.text, x, y)
            x += fm.stringWidth(fragment.text)
        }
    }

    fun updateStatistics(stats: GameStatistics) {
        line1 = statsLine1(stats)
        line2 = statsLine2(stats)
        repaint()
    }

    private fun statsLine1(stats: GameStatistics): Line {
        val line = Line()
        line.add("%-20s  St:%d Ch:%d", stats.playerName, stats.strength, stats.charisma)
        return line
    }

    private fun statsLine2(stats: GameStatistics): Line {
        val line = Line()
        line.add("%-20s  ", stats.regionName)
        line.add(getHitPointsColor(stats), "HP:%d(%d)", stats.hitPoints, stats.maximumHitPoints)
        line.add("  AC:%d  Exp:%s(%s)  T:%d    ", stats.armorClass, stats.level, stats.experience, stats.time)
        line.add(getHungerColor(stats.hungerLevel), "%-10s", stats.hungerLevel)
        return line
    }

    companion object {
        private fun getHitPointsColor(player: GameStatistics) =
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
