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

package net.wanhack.ui.console

import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import javax.swing.JComponent
import javax.swing.JOptionPane
import net.wanhack.model.common.Console
import net.wanhack.model.common.Direction
import net.wanhack.model.item.Item
import net.wanhack.utils.collections.CircularBuffer
import java.awt.Frame

class ConsoleView : JComponent(), Console {

    private val buffer = CircularBuffer<String>(100)
    private var hasOutputOnThisTurn = false
    private var currentRow = 0
    private val rowsToShow = 3;

    {
        setBackground(Color.BLACK)
        setForeground(Color.WHITE)
        setFont(Font("Monospaced", Font.PLAIN, 14))
    }

    val frame: Frame
        get() = JOptionPane.getFrameForComponent(this)!!

    override fun ask(question: String) =
        JOptionPane.showConfirmDialog(frame, question, "Question", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION

    fun scrollUp() {
        currentRow = Math.max(0, currentRow - 1)
        repaint()
    }

    fun scrollDown() {
        currentRow = Math.min(buffer.size() - 1, currentRow + 1)
        repaint()
    }

    fun paint(g: Graphics?): Unit {
        g!!
        g.setColor(getBackground())
        g.fillRect(0, 0, getWidth(), getHeight())

        if (!buffer.empty && currentRow < buffer.size) {
            g.setColor(getForeground())
            val fm = getFontMetrics(getFont())!!
            val maxWidth = getWidth()
            val text = buffer[currentRow]
            val words = text.split(" ")
            var x = 0
            var y = fm.getAscent()
            for (i in 0..words.size - 1) {
                var wordWidth = fm.stringWidth(words[i])
                if (x != 0 && x + wordWidth >= maxWidth) {
                    x = 0
                    y += fm.getHeight()
                }

                g.drawString(words[i], x, y)
                x += wordWidth + fm.charWidth(' ')
            }
        }

    }

    override fun getPreferredSize(): Dimension {
        val fm = getFontMetrics(getFont())!!
        return Dimension(200, rowsToShow * fm.getHeight())
    }

    fun turnEnd() {
        if (!hasOutputOnThisTurn)
            if (!buffer.isEmpty() && !"".equals(buffer.last()))
                buffer.add("")

        hasOutputOnThisTurn = false
        currentRow = buffer.size() - 1
        repaint()
    }

    override fun message(message: String) {
        if (hasOutputOnThisTurn) {
            var last = buffer.last()
            buffer.replaceLast(last + " " + message)
        } else {
            if (!buffer.isEmpty() && "" == buffer.last())
                buffer.replaceLast(message)
            else
                buffer.add(message)
            hasOutputOnThisTurn = true
        }
    }

    override fun selectDirection(): Direction? =
        SelectDirectionDialog.selectDirection(frame)

    override fun <T: Item> selectItem(itemType: Class<T>, message: String, items: Collection<T>): T? =
        itemType.cast(SelectItemsDialog.selectItem(frame, message, items))

    override fun selectItems(message: String, items: Collection<Item>): Set<Item> =
        SelectItemsDialog.selectItems(frame, message, items)

    fun clear() {
        buffer.clear()
        hasOutputOnThisTurn = false
    }
}
