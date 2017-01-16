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

package net.wanhack.desktop.common

import net.wanhack.desktop.extensions.label
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Frame
import javax.swing.*

class ErrorDialog(parent: Frame?, title: String, val throwable: Throwable): JDialog() {

    val okButton = JButton("Ok").apply {
        isVisible = false
    }

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
