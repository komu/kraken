package dev.komu.kraken.desktop.console

import dev.komu.kraken.desktop.extensions.makeAction
import dev.komu.kraken.desktop.extensions.set
import dev.komu.kraken.model.Direction
import java.awt.Frame
import java.awt.GridLayout
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JPanel

class SelectDirectionDialog(owner: Frame): JDialog() {

    private var selectedDirection: Direction? = null

    init {
        isModal = true
        contentPane.layout = GridLayout(3, 3)
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

        actionMap["escape"] = makeAction("Escape") {
            selectedDirection = null
            isVisible = false
        }
        pack()
        setLocationRelativeTo(owner)
    }

    fun selectDirectionButton(dir: Direction) = JButton(SelectDirectionAction(dir))

    inner class SelectDirectionAction(val dir: Direction): AbstractAction(dir.shortName) {
        override fun actionPerformed(e: ActionEvent) {
            selectedDirection = dir
            isVisible = false
        }
    }

    companion object {
        fun selectDirection(frame: Frame): Direction? {
            val dlg = SelectDirectionDialog(frame)
            dlg.isVisible = true
            return dlg.selectedDirection
        }
    }
}
