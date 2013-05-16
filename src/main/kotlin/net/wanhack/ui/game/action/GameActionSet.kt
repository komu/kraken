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

package net.wanhack.ui.game.action

import java.awt.event.ActionEvent
import java.awt.event.KeyEvent.*
import java.util.ArrayList
import javax.swing.KeyStroke
import javax.swing.Action
import net.wanhack.model.GameRef
import net.wanhack.model.Game

class GameActionSet {

    val actions = ArrayList<GameAction>()

    var gameRef: GameRef? = null
        set(gameRef: GameRef?) {
            $gameRef = gameRef

            for (action in actions)
                action.gameRef = gameRef
        }

    {
        add("Wield Weapon", VK_W, "W")      { it.wield() }
        add("Wear Armor", VK_A, "shift W")  { it.wear() }
        add("Rest", VK_R, "R")              { it.rest(-1) }
        add("Talk", VK_T, "T")              { it.talk() }
        add("Open Door", VK_O, "O")         { it.openDoor() }
        add("Close Door", VK_C, "C")        { it.closeDoor() }
        add("Pick up", VK_P, "COMMA")       { it.pickup()  }
        add("Drop", VK_D, "D")              { it.drop() }
        add("Eat", VK_E, "E")               { it.eat() }
        add("Fling", VK_F, "F")             { it.fling() }
        add("Search", VK_S, "S")            { it.search() }
    }

    fun add(name: String, mnemonic: Int, accelerator: String, action: (Game) -> Unit) {
        actions.add(object : GameAction(name) {
            {
                putValue(Action.MNEMONIC_KEY, mnemonic)
                putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator))
            }

            override fun actionPerformed(e: ActionEvent, game: Game) {
                action(game)
                game.listener()
            }
        })
    }
}
