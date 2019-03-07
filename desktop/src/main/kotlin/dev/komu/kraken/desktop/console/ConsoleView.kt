package dev.komu.kraken.desktop.console

import dev.komu.kraken.common.Direction
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
    private val rowsToShow = 3

    init {
        background = Color.BLACK
        foreground = Color.WHITE
        font = Font("Monospaced", Font.PLAIN, 14)
    }

    private val frame: Frame
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

    override fun selectDirection(): Direction? =
        SelectDirectionDialog.selectDirection(frame)

    override fun <T: Item> selectItem(message: String, items: Collection<T>) =
        SelectItemsDialog.selectItem(frame, message, items)

    fun clear() {
        buffer.clear()
        hasOutputOnThisTurn = false
    }
}
