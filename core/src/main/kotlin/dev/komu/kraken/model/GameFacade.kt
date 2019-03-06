package dev.komu.kraken.model

import dev.komu.kraken.common.Direction
import dev.komu.kraken.model.common.Console
import dev.komu.kraken.model.item.Item
import dev.komu.kraken.model.region.Coordinate
import dev.komu.kraken.utils.relinquish
import dev.komu.kraken.utils.yieldLock
import java.util.concurrent.Executors
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

/**
 * All commands from UI to game go through this facade.
 */
class GameFacade(config: GameConfiguration, console: Console, val listener: (Boolean) -> Unit) {

    private val gameExecutor = Executors.newSingleThreadExecutor { Thread(it, "game") }
    private val lock = ReentrantReadWriteLock(true)
    private val game = Game(config, LockRelinquishingConsole(console, lock.writeLock())) {
        listener(true)
        lock.writeLock().yieldLock()
    }

    fun <T> query(callback: (ReadOnlyGame) -> T): T =
        lock.readLock().withLock { callback(game) }

    fun start() = gameAction {
        game.start()
    }

    fun movePlayer(direction: Direction) = gameAction {
        game.movePlayer(direction)
    }

    fun runTowards(direction: Direction) = gameAction {
        game.runTowards(direction)
    }

    fun runTowards(c: Coordinate) = gameAction {
        game.runTowards(c)
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

    fun equip() = gameAction {
        game.equip()
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
        game.throwItem()
    }

    fun search() = gameAction {
        game.search()
    }

    fun focus(coordinate: Coordinate) = gameAction {
        game.selectedCell = coordinate
    }

    private fun gameAction(body: () -> Unit) {
        gameExecutor.execute {
            lock.writeLock().withLock {
                if (!game.over)
                    body()
            }

            listener(false)
        }
    }

    class LockRelinquishingConsole(private val console: Console, private val lock: Lock) : Console {
        override fun message(message: String) =
            console.message(message)

        override fun ask(question: String) =
            lock.relinquish { console.ask(question) }

        override fun selectDirection() =
            lock.relinquish { console.selectDirection() }

        override fun <T: Item> selectItem(message: String, items: Collection<T>) =
            lock.relinquish { console.selectItem(message, items) }
    }
}
