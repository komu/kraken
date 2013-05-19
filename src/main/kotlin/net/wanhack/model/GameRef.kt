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

import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.concurrent.Executors
import kotlin.concurrent.withLock

class GameRef(private val game: Game) {

    private val gameExecutor = Executors.newSingleThreadExecutor()
    private val lock = ReentrantReadWriteLock(true)

    fun scheduleAction(callback: (Game) -> Unit) {
        gameExecutor.execute(Runnable() {
            lock.writeLock().withLock {
                callback(game)
            }
        })
    }

    fun executeQuery<T>(callback: (ReadOnlyGame) -> T): T =
        lock.readLock().withLock {
            callback(game)
        }

    fun withoutLock<T>(callback: () -> T): T =
        try {
            lock.writeLock().unlock()
            callback()
        } finally {
            lock.writeLock().lock()
        }

    fun yieldWriteLock()  {
        if (lock.isWriteLockedByCurrentThread()) {
            lock.writeLock().unlock()
            lock.writeLock().lock()
        }
    }

    fun assertWriteLockedByCurrentThread() {
        assert(lock.isWriteLockedByCurrentThread()) { "Write lock not held by current thread: ${Thread.currentThread().getName()}" }
    }
}
