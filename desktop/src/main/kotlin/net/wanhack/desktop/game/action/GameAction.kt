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

package net.wanhack.desktop.game.action

import net.wanhack.model.GameFacade
import java.awt.event.ActionEvent
import javax.swing.AbstractAction

abstract class GameAction(name: String, game: GameFacade? = null): AbstractAction(name) {

    var game: GameFacade? = game
        set(game) {
            field = game
            isEnabled = game != null
        }

    init {
        isEnabled = false
    }

    override fun actionPerformed(e: ActionEvent) {
        val g = game
        if (g != null)
            actionPerformed(e, g)
    }

    protected abstract fun actionPerformed(e: ActionEvent, game: GameFacade)
}
