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

package net.wanhack.desktop.debug

import java.awt.BorderLayout
import java.awt.event.*
import java.util.logging.*
import javax.swing.*

class LogFrame : JFrame() {
    private val textArea = JTextArea()
    private val handler = FrameLogHandler()
    private val logger = Logger.getLogger("net.wanhack");

    {
        logger.setLevel(Level.INFO)
        logger.addHandler(handler)
        textArea.setEditable(false)
        textArea.setLineWrap(true)
        textArea.setWrapStyleWord(true)
        textArea.setColumns(80)
        textArea.setRows(15)
        getContentPane()?.setLayout(BorderLayout())
        add(JScrollPane(textArea), BorderLayout.CENTER)
        add(createToolBar(), BorderLayout.NORTH)
        setLocationByPlatform(true)
        pack()
    }

    fun createToolBar() : JToolBar {
        val toolBar = JToolBar()
        toolBar.setFloatable(false)
        toolBar.add(ClearLogAction())
        toolBar.addSeparator()
        toolBar.add(createLogLevelSwitcher())
        return toolBar
    }

    fun createLogLevelSwitcher() : JComponent {
        val levels = array<Level>(Level.ALL, Level.FINEST, Level.FINER, Level.FINE, Level.CONFIG, Level.INFO, Level.WARNING, Level.SEVERE, Level.OFF)
        val comboBox = JComboBox(levels)
        comboBox.setSelectedItem(logger.getLevel())
        comboBox.setPrototypeDisplayValue(Level.WARNING)
        comboBox.setMaximumRowCount(10)
        comboBox.setMaximumSize(comboBox.getPreferredSize())
        comboBox.addActionListener(ActionListener {
            logger.setLevel(comboBox.getSelectedItem() as Level)
        })
        return comboBox
    }

    inner class FrameLogHandler : Handler() {
        private val formatter = SimpleFormatter()

        override fun publish(record : LogRecord?) {
            if (isLoggable(record!!)) {
                val formatted = formatter.format(record)
                textArea.setText(textArea.getText() + formatted)
            }

        }

        override fun flush() { }
        override fun close() { }
    }

    inner class ClearLogAction : AbstractAction("Clear") {
        public override fun actionPerformed(e: ActionEvent) {
            textArea.setText("")
        }
    }
}
