package dev.komu.kraken.desktop.game

import dev.komu.kraken.model.item.ItemInfo
import java.awt.*
import javax.swing.JComponent
import javax.swing.JList
import javax.swing.ListCellRenderer
import javax.swing.UIManager
import javax.swing.border.EmptyBorder

class InventoryCellRenderer: JComponent(), ListCellRenderer<ItemInfo> {

    private var item: ItemInfo? = null
    private var title = ""
    private var description = ""
    private val titleFont = Font("SansSerif", Font.BOLD, 13)
    private val descriptionFont = Font("SansSerif", Font.PLAIN, 11)

    override fun getPreferredSize(): Dimension {
        val fm1 = getFontMetrics(titleFont)!!
        val fm2 = getFontMetrics(descriptionFont)!!
        val left = Math.max(fm1.stringWidth(" % "), fm1.stringWidth(" M "))
        val width = left + (Math.max(fm1.stringWidth(title), fm2.stringWidth(description)))
        return Dimension(width, fm1.height + fm2.height)
    }

    override fun getListCellRendererComponent(list: JList<out ItemInfo>, value: ItemInfo?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
        this.item = value
        this.title = item!!.title
        this.description = item!!.description

        if (isSelected) {
            background = list.selectionBackground
            foreground = list.selectionForeground
        } else {
            background = list.background
            foreground = list.foreground
        }

        if (cellHasFocus)
            border = UIManager.getBorder("List.focusCellHighlightBorder")
        else
            border = EmptyBorder(1, 1, 1, 1)

        return this
    }

    override fun paint(g: Graphics) {
        g.color = background
        g.fillRect(0, 0, width, height)
        g.color = (if (item!!.inUse) Color.WHITE else Color.LIGHT_GRAY)
        val fm1 = getFontMetrics(titleFont)!!
        val fm2 = getFontMetrics(descriptionFont)!!
        val left = Math.max(fm1.stringWidth(" % "), fm1.stringWidth(" M "))
        g.font = titleFont
        g.drawString(item!!.letter.toString(), 2, (fm1.ascent))
        g.drawString(title, left, fm1.ascent)
        g.font = descriptionFont
        g.drawString(description, left, fm1.height + fm2.ascent)
    }
}
