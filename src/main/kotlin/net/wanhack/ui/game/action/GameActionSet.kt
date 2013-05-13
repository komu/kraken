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
import java.awt.event.KeyEvent
import java.util.ArrayList
import javax.swing.KeyStroke
import javax.swing.Action.MNEMONIC_KEY
import javax.swing.Action.ACCELERATOR_KEY
import net.wanhack.model.GameRef
import net.wanhack.model.IGame

class GameActionSet {

    val actions = ArrayList<GameAction>()

    var gameRef: GameRef? = null
        set(gameRef: GameRef?) {
            $gameRef = gameRef

            for (action in actions)
                action.gameRef = gameRef
        }

    {
        actions.add(WieldAction())
        actions.add(WearAction())
        actions.add(RestAction())
        actions.add(TalkAction())
        actions.add(OpenDoorAction())
        actions.add(CloseDoorAction())
        actions.add(PickupAction())
        actions.add(DropAction())
        actions.add(EatAction())
        actions.add(FlingAction())
        actions.add(SearchAction())
    }

    private class WieldAction: GameAction("Wield Weapon") {
        {
            putValue(MNEMONIC_KEY, KeyEvent.VK_W)
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("W"))
        }

        protected override fun actionPerformed(e: ActionEvent, game: IGame) {
            game.wield()
        }
    }
    
    private class WearAction: GameAction("Wear Armor") {
        {
            putValue(MNEMONIC_KEY, KeyEvent.VK_A)
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("shift W"))
        }
        
        protected override fun actionPerformed(e: ActionEvent, game: IGame) {
            game.wear()
        }
    }
    
    private class RestAction: GameAction("Rest") {
        {
            putValue(MNEMONIC_KEY, KeyEvent.VK_R)
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("R"))
        }

        protected override fun actionPerformed(e: ActionEvent, game: IGame) {
            game.rest(-1)
        }
    }
    
    private class TalkAction: GameAction("Talk") {
        {
            putValue(MNEMONIC_KEY, KeyEvent.VK_T)
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("T"))
        }

        protected override fun actionPerformed(e: ActionEvent, game: IGame) {
            game.talk()
        }
    }
    
    private class OpenDoorAction: GameAction("Open Door") {
        {
            putValue(MNEMONIC_KEY, KeyEvent.VK_O)
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("O"))
        }
        
        protected override fun actionPerformed(e: ActionEvent, game: IGame) {
            game.openDoor()
        }
        

    }
    private class CloseDoorAction: GameAction("Close Door") {
        {
            putValue(MNEMONIC_KEY, KeyEvent.VK_C)
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("C"))
        }

        protected override fun actionPerformed(e: ActionEvent, game: IGame) {
            game.closeDoor()
        }
    }

    private class PickupAction: GameAction("Pick up") {
        {
            putValue(MNEMONIC_KEY, KeyEvent.VK_P)
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("COMMA"))
        }

        protected override fun actionPerformed(e: ActionEvent, game: IGame) {
            game.pickup()
        }
    }

    private class DropAction: GameAction("Drop") {
        {
            putValue(MNEMONIC_KEY, KeyEvent.VK_D)
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("D"))
        }

        protected override fun actionPerformed(e: ActionEvent, game: IGame) {
            game.drop()
        }
    }

    private class EatAction: GameAction("Eat") {
        {
            putValue(MNEMONIC_KEY, KeyEvent.VK_E)
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("E"))
        }

        protected override fun actionPerformed(e: ActionEvent, game: IGame) {
            game.eat()
        }
    }

    private class FlingAction: GameAction("Fling") {
        {
            putValue(MNEMONIC_KEY, KeyEvent.VK_F)
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("F"))
        }

        protected override fun actionPerformed(e: ActionEvent, game: IGame) {
            game.fling()
        }
    }

    private class SearchAction: GameAction("Search") {
        {
            putValue(MNEMONIC_KEY, KeyEvent.VK_S)
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("S"))
        }

        protected override fun actionPerformed(e: ActionEvent, game: IGame) {
            game.search()
        }
    }
}
