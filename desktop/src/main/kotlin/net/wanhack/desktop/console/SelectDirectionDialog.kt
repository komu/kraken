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

package net.wanhack.desktop.console

import java.awt.Frame
import java.awt.GridLayout
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JPanel
import kotlin.swing.action
import net.wanhack.common.Direction
import net.wanhack.desktop.extensions.*

private class SelectDirectionDialog(owner: Frame): JDialog() {

    private var selectedDirection: Direction? = null;

    {
        val contentPane = getContentPane()!!
        val rootPane = getRootPane()!!

        setModal(true)
        contentPane.setLayout(GridLayout(3, 3))
        add(selectDirectionButton(Direction.NW))
        add(selectDirectionButton(Direction.NORTH))
        add(selectDirectionButton(Direction.NE))
        add(selectDirectionButton(Direction.WEST))
        add(JPanel())
        add(selectDirectionButton(Direction.EAST))
        add(selectDirectionButton(Direction.SW))
        add(selectDirectionButton(Direction.SOUTH))
        add(selectDirectionButton(Direction.SE))

        val inputMap = rootPane.inputMap
        inputMap["UP"]      = Direction.NORTH
        inputMap["DOWN"]    = Direction.SOUTH
        inputMap["LEFT"]    = Direction.WEST
        inputMap["RIGHT"]   = Direction.EAST
        inputMap["NUMPAD1"] = Direction.SW
        inputMap["NUMPAD2"] = Direction.SOUTH
        inputMap["NUMPAD3"] = Direction.SE
        inputMap["NUMPAD4"] = Direction.WEST
        inputMap["NUMPAD6"] = Direction.EAST
        inputMap["NUMPAD7"] = Direction.NW
        inputMap["NUMPAD8"] = Direction.NORTH
        inputMap["NUMPAD9"] = Direction.NE
        inputMap["ESCAPE"] = "escape"

        val actionMap = rootPane.actionMap
        for (dir in Direction.values())
            actionMap[dir] = SelectDirectionAction(dir)

        actionMap["escape"] = action("Escape") {
            selectedDirection = null
            setVisible(false)
        }
        pack()
        setLocationRelativeTo(owner)
    }

    fun selectDirectionButton(dir: Direction) = JButton(SelectDirectionAction(dir))

    inner class SelectDirectionAction(val dir: Direction): AbstractAction(dir.shortName) {
        public override fun actionPerformed(e: ActionEvent) {
            selectedDirection = dir
            setVisible(false)
        }
    }

    class object {
        fun selectDirection(frame: Frame): Direction? {
            val dlg = SelectDirectionDialog(frame)
            dlg.setVisible(true)
            return dlg.selectedDirection
        }
    }
}
