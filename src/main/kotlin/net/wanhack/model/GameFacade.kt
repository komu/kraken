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

import net.wanhack.model.common.Direction
import net.wanhack.model.item.Item
import net.wanhack.model.common.Console
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.concurrent.locks.Lock
import kotlin.concurrent.withLock
import net.wanhack.utils.relinquish
import net.wanhack.utils.yield

/**
 * All commands from UI to game go through this facade.
 */
class GameFacade(config: GameConfiguration, wizardMode: Boolean, console: Console, callback: () -> Unit) {

    private val lock = ReentrantReadWriteLock(true)
    private val game = Game(config, wizardMode, LockRelinquishingConsole(console, lock.writeLock()));

    {
        game.listener = callback
    }

    fun query<T>(callback: (ReadOnlyGame) -> T): T =
        lock.readLock().withLock { callback(game) }

    fun yieldWriteLock() {
        lock.writeLock().yield()
    }

    fun start() = gameAction {
        game.start()
    }

    fun movePlayer(direction: Direction) = gameAction {
        game.movePlayer(direction)
    }

    fun runTowards(direction: Direction) = gameAction {
        game.runTowards(direction)
    }

    fun movePlayerVertically(up: Boolean) = gameAction {
        game.movePlayerVertically(up)
    }

    fun skipTurn() = gameAction {
        game.skipTurn()
    }

    fun revealCurrentRegion() = gameAction {
        game.revealCurrentRegion()
    }

    fun drop(item: Item) = gameAction {
        game.drop(item)
    }

    fun wield() = gameAction {
        game.wield()
    }

    fun wear() = gameAction {
        game.wear()
    }

    fun rest(maxTurns: Int) = gameAction {
        game.rest(maxTurns)
    }

    fun talk() = gameAction {
        game.talk()
    }

    fun openDoor() = gameAction {
        game.openDoor()
    }

    fun closeDoor() = gameAction {
        game.closeDoor()
    }

    fun pickup() = gameAction {
        game.pickup()
    }

    fun drop() = gameAction {
        game.drop()
    }

    fun eat() = gameAction {
        game.eat()
    }

    fun fling() = gameAction {
        game.fling()
    }

    fun search() = gameAction {
        game.search()
    }

    fun notifyListener() =
        game.listener()

    private fun gameAction(body: () -> Unit) {
        lock.writeLock().withLock {
            if (!game.over)
                body()
        }

        notifyListener()
    }

    class LockRelinquishingConsole(val console: Console, val lock: Lock) : Console {
        override fun message(message: String) =
            console.message(message)

        override fun ask(question: String) =
            lock.relinquish { console.ask(question) }

        override fun selectDirection() =
            lock.relinquish { console.selectDirection() }

        override fun <T: Item> selectItem(itemType: Class<T>, message: String, items: Collection<T>) =
            lock.relinquish { console.selectItem(itemType, message, items) }

        override fun selectItems(message: String, items: Collection<Item>) =
            lock.relinquish { console.selectItems(message, items) }
    }
}
