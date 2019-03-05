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

import dev.komu.kraken.desktop.extensions.makeAction
import dev.komu.kraken.desktop.extensions.set
import java.awt.Frame
import java.awt.GridLayout
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JPanel

class SelectDirectionDialog(owner: Frame): JDialog() {

    private var selectedDirection: dev.komu.kraken.common.Direction? = null

    init {
        isModal = true
        contentPane.layout = GridLayout(3, 3)
        add(selectDirectionButton(dev.komu.kraken.common.Direction.NW))
        add(selectDirectionButton(dev.komu.kraken.common.Direction.NORTH))
        add(selectDirectionButton(dev.komu.kraken.common.Direction.NE))
        add(selectDirectionButton(dev.komu.kraken.common.Direction.WEST))
        add(JPanel())
        add(selectDirectionButton(dev.komu.kraken.common.Direction.EAST))
        add(selectDirectionButton(dev.komu.kraken.common.Direction.SW))
        add(selectDirectionButton(dev.komu.kraken.common.Direction.SOUTH))
        add(selectDirectionButton(dev.komu.kraken.common.Direction.SE))

        val inputMap = rootPane.inputMap
        inputMap["UP"]      = dev.komu.kraken.common.Direction.NORTH
        inputMap["DOWN"]    = dev.komu.kraken.common.Direction.SOUTH
        inputMap["LEFT"]    = dev.komu.kraken.common.Direction.WEST
        inputMap["RIGHT"]   = dev.komu.kraken.common.Direction.EAST
        inputMap["NUMPAD1"] = dev.komu.kraken.common.Direction.SW
        inputMap["NUMPAD2"] = dev.komu.kraken.common.Direction.SOUTH
        inputMap["NUMPAD3"] = dev.komu.kraken.common.Direction.SE
        inputMap["NUMPAD4"] = dev.komu.kraken.common.Direction.WEST
        inputMap["NUMPAD6"] = dev.komu.kraken.common.Direction.EAST
        inputMap["NUMPAD7"] = dev.komu.kraken.common.Direction.NW
        inputMap["NUMPAD8"] = dev.komu.kraken.common.Direction.NORTH
        inputMap["NUMPAD9"] = dev.komu.kraken.common.Direction.NE
        inputMap["ESCAPE"] = "escape"

        val actionMap = rootPane.actionMap
        for (dir in dev.komu.kraken.common.Direction.values())
            actionMap[dir] = SelectDirectionAction(dir)

        actionMap["escape"] = makeAction("Escape") {
            selectedDirection = null
            isVisible = false
        }
        pack()
        setLocationRelativeTo(owner)
    }

    fun selectDirectionButton(dir: dev.komu.kraken.common.Direction) = JButton(SelectDirectionAction(dir))

    inner class SelectDirectionAction(val dir: dev.komu.kraken.common.Direction): AbstractAction(dir.shortName) {
        override fun actionPerformed(e: ActionEvent) {
            selectedDirection = dir
            isVisible = false
        }
    }

    companion object {
        fun selectDirection(frame: Frame): dev.komu.kraken.common.Direction? {
            val dlg = SelectDirectionDialog(frame)
            dlg.isVisible = true
            return dlg.selectedDirection
        }
    }
}
