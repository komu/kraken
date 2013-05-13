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

import java.awt.Frame
import java.awt.GridLayout
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JPanel
import javax.swing.KeyStroke
import net.wanhack.model.common.Direction

class SelectDirectionDialog(owner: Frame): JDialog() {

    private var selectedDirection: Direction? = null;

    {
        setModal(true)
        getContentPane()!!.setLayout(GridLayout(3, 3))
        add(JButton(SelectDirectionAction(Direction.NW)))
        add(JButton(SelectDirectionAction(Direction.NORTH)))
        add(JButton(SelectDirectionAction(Direction.NE)))
        add(JButton(SelectDirectionAction(Direction.WEST)))
        add(JPanel())
        add(JButton(SelectDirectionAction(Direction.EAST)))
        add(JButton(SelectDirectionAction(Direction.SW)))
        add(JButton(SelectDirectionAction(Direction.SOUTH)))
        add(JButton(SelectDirectionAction(Direction.SE)))
        addInput("UP", Direction.NORTH)
        addInput("DOWN", Direction.SOUTH)
        addInput("LEFT", Direction.WEST)
        addInput("RIGHT", Direction.EAST)
        addInput("NUMPAD1", Direction.SW)
        addInput("NUMPAD2", Direction.SOUTH)
        addInput("NUMPAD3", Direction.SE)
        addInput("NUMPAD4", Direction.WEST)
        addInput("NUMPAD6", Direction.EAST)
        addInput("NUMPAD7", Direction.NW)
        addInput("NUMPAD8", Direction.NORTH)
        addInput("NUMPAD9", Direction.NE)
        addInput("ESCAPE", "escape")
        var actionMap = getRootPane()?.getActionMap()!!
        for (dir in Direction.values())
            actionMap.put(dir, SelectDirectionAction(dir))

        actionMap.put("escape", SelectDirectionAction(null))
        pack()
        setLocationRelativeTo(owner)
    }

    private fun addInput(keyStroke: String, actionKey: Any) {
        val inputMap = getRootPane()?.getInputMap()!!
        inputMap.put(KeyStroke.getKeyStroke(keyStroke), actionKey)
    }

    inner class SelectDirectionAction(val dir: Direction?): AbstractAction(dir?.shortName ?: "") {
        public override fun actionPerformed(e: ActionEvent) {
            selectedDirection = dir
            setVisible(false)
        }
    }

    class object {
        fun selectDirection(frame: Frame): Direction? {
            var dlg = SelectDirectionDialog(frame)
            dlg.setVisible(true)
            return dlg.selectedDirection
        }
    }
}
