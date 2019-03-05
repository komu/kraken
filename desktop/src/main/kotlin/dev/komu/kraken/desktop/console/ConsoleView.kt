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

package dev.komu.kraken.desktop.console

import dev.komu.kraken.model.common.Console
import dev.komu.kraken.model.item.Item
import dev.komu.kraken.utils.collections.CircularBuffer
import java.awt.*
import javax.swing.JComponent
import javax.swing.JOptionPane

class ConsoleView : JComponent(), Console {

    private val buffer = CircularBuffer<String>(100)
    private var hasOutputOnThisTurn = false
    private var currentRow = 0
    private val rowsToShow = 3;

    init {
        background = Color.BLACK
        foreground = Color.WHITE
        font = Font("Monospaced", Font.PLAIN, 14)
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
        currentRow = Math.min(buffer.size - 1, currentRow + 1)
        repaint()
    }

    override fun paint(g: Graphics) {
        g.color = background
        g.fillRect(0, 0, width, height)

        if (!buffer.isEmpty() && currentRow < buffer.size) {
            g.color = foreground
            val fm = getFontMetrics(font)
            val maxWidth = width
            val text = buffer[currentRow]
            val words = text.split(" ")
            var x = 0
            var y = fm.ascent
            for (word in words) {
                val wordWidth = fm.stringWidth(word)
                if (x != 0 && x + wordWidth >= maxWidth) {
                    x = 0
                    y += fm.height
                }

                g.drawString(word, x, y)
                x += wordWidth + fm.charWidth(' ')
            }
        }

    }

    override fun getPreferredSize(): Dimension {
        val fm = getFontMetrics(font)
        return Dimension(200, rowsToShow * fm.height)
    }

    fun turnEnd() {
        if (!hasOutputOnThisTurn)
            if (!buffer.isEmpty() && "" != buffer.last())
                buffer.add("")

        hasOutputOnThisTurn = false
        currentRow = buffer.size - 1
        repaint()
    }

    override fun message(message: String) {
        if (hasOutputOnThisTurn) {
            val last = buffer.last()
            buffer.replaceLast("$last $message")
        } else {
            if (!buffer.isEmpty() && "" == buffer.last())
                buffer.replaceLast(message)
            else
                buffer.add(message)
            hasOutputOnThisTurn = true
        }
    }

    override fun selectDirection(): dev.komu.kraken.common.Direction? =
        SelectDirectionDialog.selectDirection(frame)

    override fun <T: Item> selectItem(message: String, items: Collection<T>) =
        SelectItemsDialog.selectItem(frame, message, items)

    override fun <T : Item> selectItems(message: String, items: Collection<T>) =
        SelectItemsDialog.selectItems(frame, message, items)

    fun clear() {
        buffer.clear()
        hasOutputOnThisTurn = false
    }
}
