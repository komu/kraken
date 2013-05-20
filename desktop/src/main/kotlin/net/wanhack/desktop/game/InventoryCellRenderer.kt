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

package net.wanhack.desktop.game

import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
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
