package dev.komu.kraken.desktop.game.action

import dev.komu.kraken.model.GameFacade
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent.*
import java.util.*
import javax.swing.Action
import javax.swing.KeyStroke

class GameActionSet {

    val actions = ArrayList<GameAction>()

    var gameFacade: GameFacade? = null
        set(gameFacade) {
            field = gameFacade

            for (action in actions)
                action.game = gameFacade
        }

    init {
        add("Equip", VK_W, "shift E")       { it.equip() }
        add("Rest", VK_R, "R")              { it.rest() }
        add("Talk", VK_T, "T")              { it.talk() }
        add("Open Door", VK_O, "O")         { it.openDoor() }
        add("Close Door", VK_C, "C")        { it.closeDoor() }
        add("Pick up", VK_P, "COMMA")       { it.pickup()  }
        add("Drop", VK_D, "D")              { it.drop() }
        add("Eat", VK_E, "E")               { it.eat() }
        add("Fling", VK_F, "F")             { it.fling() }
        add("Search", VK_S, "S")            { it.search() }
    }

    fun add(name: String, mnemonic: Int, accelerator: String, action: (GameFacade) -> Unit) {
        actions.add(object : GameAction(name) {
            init {
                putValue(Action.MNEMONIC_KEY, mnemonic)
                putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator))
            }

            override fun actionPerformed(e: ActionEvent, game: GameFacade) {
                action(game)
            }
        })
    }
}
