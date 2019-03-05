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

package dev.komu.kraken.desktop.game.action

import dev.komu.kraken.model.GameFacade
import dev.komu.kraken.model.item.Item
import java.awt.event.ActionEvent

class DropItemAction(game: GameFacade?, val item: Item): GameAction("Drop", game) {

    override fun actionPerformed(e: ActionEvent, game: GameFacade) {
        game.drop(item)
    }
}
