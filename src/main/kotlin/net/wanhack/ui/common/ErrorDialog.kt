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

package net.wanhack.ui.common

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent

class ErrorDialog(parent: Frame?, title: String, val throwable: Throwable): JDialog() {

    {
        setModal(true)
        setResizable(false)
        setLayout(BorderLayout())
        add(createDetailPane(), BorderLayout.CENTER)
        add(createButtonPane(), BorderLayout.SOUTH)
        pack()
        setLocationRelativeTo(parent)
    }

    private fun createDetailPane(): JPanel {
        val panel = JPanel(FlowLayout(FlowLayout.LEFT))
        val defaults = UIManager.getDefaults()
        val errorIcon = defaults?.getIcon("OptionPane.errorIcon")
        val icon = JLabel(errorIcon, SwingConstants.LEFT)
        icon.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5))
        panel.add(icon)
        val text = JLabel(throwable.toString())
        text.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10))
        panel.add(text)

        val preferred = panel.getPreferredSize()!!

        preferred.width = Math.max(preferred.width, 250)
        panel.setPreferredSize(preferred)
        return panel
    }

    private fun createButtonPane(): JPanel {
        val panel = JPanel(FlowLayout(FlowLayout.CENTER))
        val okButton = JButton(OkAction())
        getRootPane()?.setDefaultButton(okButton)
        panel.add(okButton)
        return panel
    }

    private inner class OkAction(): AbstractAction("Ok") {
        override fun actionPerformed(e: ActionEvent) {
            setVisible(false)
        }
    }

    class object {
        public open fun show(frame: Frame?, title: String, exception: Throwable) {
            ErrorDialog(frame, title, exception).setVisible(true)
        }
    }
}
