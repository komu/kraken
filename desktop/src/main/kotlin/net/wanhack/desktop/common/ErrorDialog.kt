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

import javax.swing.*
import java.awt.*
import kotlin.swing.*
import net.wanhack.desktop.extensions.*

class ErrorDialog(parent: Frame?, title: String, val throwable: Throwable): JDialog() {

    val okButton = button("Ok") {
        setVisible(false)
    };

    {
        setModal(true)
        setResizable(false)
        setLayout(BorderLayout())
        add(createDetailPane(), BorderLayout.CENTER)
        add(createButtonPane(), BorderLayout.SOUTH)
        getRootPane()?.setDefaultButton(okButton)
        pack()
        setLocationRelativeTo(parent)
    }

    private fun createDetailPane() =
        panel {
            setLayout(FlowLayout(FlowLayout.LEFT))
            add(label {
                icon = UIManager.getDefaults()?.getIcon("OptionPane.errorIcon")
                horizontalAlignment = SwingConstants.LEFT
                border = BorderFactory.createEmptyBorder(10, 10, 10, 5)
            })

            add(label(throwable.toString()) {
                border = BorderFactory.createEmptyBorder(10, 5, 10, 10)
            })

            preferredWidth = Math.max(preferredWidth, 250)
        }

    private fun createButtonPane() =
        panel {
            setLayout(FlowLayout(FlowLayout.CENTER))
            add(okButton)
        }

    class object {
        fun show(frame: Frame?, title: String, exception: Throwable) {
            ErrorDialog(frame, title, exception).setVisible(true)
        }
    }
}
