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

package net.wanhack.ui.debug

import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.io.Writer
import javax.swing.AbstractAction
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JTextField
import net.wanhack.model.IGame
import org.python.util.InteractiveInterpreter

class ScriptFrame : JFrame() {

    private val textArea = JTextArea()
    private val interpreter = InteractiveInterpreter()
    private val inputField = JTextField(60)
    private val writer = TextAreaWriter(textArea);

    {
        interpreter.setOut(writer)
        interpreter.setErr(writer)
        textArea.setFocusable(false)
        textArea.setEditable(false)
        textArea.setLineWrap(true)
        textArea.setWrapStyleWord(true)
        textArea.setColumns(80)
        textArea.setRows(15)
        getContentPane()?.setLayout(BorderLayout())
        add(JScrollPane(textArea), BorderLayout.CENTER)
        add(createInputBar(), BorderLayout.SOUTH)
        setLocationByPlatform(true)
        pack()
    }

    fun createInputBar(): JPanel {
        val executeButton = JButton(ExecuteAction())
        getRootPane()!!.setDefaultButton(executeButton)
        val panel = JPanel(FlowLayout())
        panel.add(inputField)
        panel.add(executeButton)
        return panel
    }

    fun execute(code: String) {
        try {
            val fail = interpreter.runsource(code)
            if (fail)
                writer.write("error: incomplete input")
        } catch (e: Exception) {
            writer.write(e.toString())
        }
    }

    fun setGame(game: IGame?) {
        interpreter.set("game", game)
        if (game != null) {
            interpreter.set("player", game.player)
        }
    }

    inner class ExecuteAction : AbstractAction("Execute") {
        override fun actionPerformed(e: ActionEvent) {
            val code = inputField.getText()!!
            inputField.setText("")
            if (code != "")
                execute(code)
        }
    }

    class TextAreaWriter(val textArea: JTextArea) : Writer() {

        override fun write(str: String) {
            textArea.setText(textArea.getText() + str)
        }

        override fun write(str: String, off: Int, len: Int) {
            write(str.substring(off, off + len))
        }

        override fun write(cbuf: CharArray, off: Int, len: Int) {
            write(String(cbuf), off, len)
        }

        public override fun flush() {}

        public override fun close() {}
    }
}
