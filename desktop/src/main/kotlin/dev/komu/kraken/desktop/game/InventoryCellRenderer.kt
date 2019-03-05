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
        return Dimension(width, fm1.getHeight() + fm2.getHeight())
    }

    override fun getListCellRendererComponent(list: JList<out ItemInfo>, value: ItemInfo?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
        this.item = value;
        this.title = item!!.title;
        this.description = item!!.description;

        if (isSelected) {
            setBackground(list.getSelectionBackground())
            setForeground(list.getSelectionForeground())
        } else {
            setBackground(list.getBackground())
            setForeground(list.getForeground())
        }

        if (cellHasFocus)
            setBorder(UIManager.getBorder("List.focusCellHighlightBorder"))
        else
            setBorder(EmptyBorder(1, 1, 1, 1))

        return this
    }

    override fun paint(g: Graphics) {
        g.setColor(getBackground())
        g.fillRect(0, 0, getWidth(), getHeight())
        g.setColor((if (item!!.inUse) Color.WHITE else Color.LIGHT_GRAY))
        val fm1 = getFontMetrics(titleFont)!!
        val fm2 = getFontMetrics(descriptionFont)!!
        val left = Math.max(fm1.stringWidth(" % "), fm1.stringWidth(" M "))
        g.setFont(titleFont)
        g.drawString(item!!.letter.toString(), 2, (fm1.getAscent()))
        g.drawString(title, left, fm1.getAscent())
        g.setFont(descriptionFont)
        g.drawString(description, left, fm1.getHeight() + fm2.getAscent())
    }
}
