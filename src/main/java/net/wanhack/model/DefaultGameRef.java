/*
 *  Copyright 2005 The Wanhack Team
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.wanhack.model;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultGameRef implements GameRef {

    private final IGame game;
    private final IGame autoLockedGame;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    private static final Executor gameExecutor = 
        Executors.newSingleThreadExecutor();
    
    public DefaultGameRef(Game game) {
        this.game = GameProxy.getProxiedGame(game, this);
        this.autoLockedGame = LockProxy.getProxiedGame(this);
    }
    
    public IGame getAutolockingGame() {
        return autoLockedGame;
    }
    
    public void scheduleAction(final ActionCallback callback) {
        gameExecutor.execute(new Runnable() {
            public void run() {
                lock.writeLock().lock();
                try {
                    callback.execute(game);
                } finally {
                    lock.writeLock().unlock();
                }
            }
        });
    }
    
    public <T extends Exception> void executeQuery(QueryCallback<T> callback) throws T {
        lock.readLock().lock();
        try {
            callback.execute(game);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    void unlockWriteLock() {
        lock.writeLock().unlock();
    }
    
    void lockWriteLock() {
        lock.writeLock().lock();
    }

    /**
     * If current thread holds the write lock, releases it for a moment,
     * then regrabs it. This lets waiting readers to performs their duties.
     */
    public void yieldWriteLock() {
        if (lock.isWriteLockedByCurrentThread()) {
            // Because 'lock' is constructed as a fair lock, regrabbing after
            // releasing should block if there were readers waiting for the lock.
            lock.writeLock().unlock();
            lock.writeLock().lock();
        }
    }

    public void assertWriteLockedByCurrentThread() {
        assert lock.isWriteLockedByCurrentThread() : 
            "Write lock not held by current thread: " + Thread.currentThread().getName();
    }
}
