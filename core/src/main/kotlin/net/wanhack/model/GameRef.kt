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

package net.wanhack.model

import java.util.concurrent.Executors

class GameRef(val game: GameFacade) {

    private val gameExecutor = Executors.newSingleThreadExecutor()

    fun scheduleAction(callback: (GameFacade) -> Unit) {
        gameExecutor.execute(Runnable() {
            callback(game)
        })
    }

    fun executeQuery<T>(callback: (ReadOnlyGame) -> T): T =
        game.query(callback)

    fun yieldWriteLock()  {
        game.yieldWriteLock()
    }
}
