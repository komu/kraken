package dev.komu.kraken.desktop.common

import dev.komu.kraken.desktop.extensions.label
import dev.komu.kraken.desktop.extensions.makeAction
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Frame
import javax.swing.*

class ErrorDialog(parent: Frame?, title: String, val throwable: Throwable): JDialog() {

    val okButton = JButton(makeAction("Ok") {
        isVisible = false
    })

    init {
        this.title = title
        isModal = true
        isResizable = false
        layout = BorderLayout()
        add(createDetailPane(), BorderLayout.CENTER)
        add(createButtonPane(), BorderLayout.SOUTH)
        rootPane.defaultButton = okButton
        pack()
        setLocationRelativeTo(parent)
    }

    private fun createDetailPane() =
        JPanel().apply {
            layout = FlowLayout(FlowLayout.LEFT)
            add(label {
                icon = UIManager.getDefaults()?.getIcon("OptionPane.errorIcon")
                horizontalAlignment = SwingConstants.LEFT
                border = BorderFactory.createEmptyBorder(10, 10, 10, 5)
            })

            add(label(throwable.toString()) {
                border = BorderFactory.createEmptyBorder(10, 5, 10, 10)
            })

            val ps = preferredSize
            preferredSize = Dimension(ps.width.coerceAtLeast(250), ps.height)
        }

    private fun createButtonPane() =
        JPanel().apply {
            layout = FlowLayout(FlowLayout.CENTER)
            add(okButton)
        }

    companion object {
        fun show(frame: Frame?, title: String, exception: Throwable) {
            val dialog = ErrorDialog(frame, title, exception)
            dialog.isVisible = true
        }
    }
}
