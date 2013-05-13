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

/**
 * A thread-safe reference to the game. Reference to the game itself should
 * not be passed between threads nor stored in instance variables, since it
 * may not be accessed without acquiring the game lock.
 * <p>
 * Access to the game happens with executing either <em>actions</em> or
 * <em>queries</em>, which acquire according lock. 
 */
public interface GameRef {
    
    /**
     * Executes an action that can change the state of the game.
     * The given action will be scheduled for execution in the 
     * game thread and it is guaranteed that only one action is
     * performed at the time. This method will just schedule the
     * action and will return immediately.
     * <p>
     * Use this method to schedule all game actions.
     */
    void scheduleAction(ActionCallback callback);
    
    /**
     * Executes a query against the domain model. The query must
     * not change the domain model in any way and will be executed
     * in current thread. It is guaranteed that no actions are
     * in progress while queries are being executed.
     */
    <E extends Exception> void executeQuery(QueryCallback<E> callback) throws E;
    
    /**
     * Returns a game-reference where read-calls will automatically grab
     * a read-lock and write-calls will automatically be scheduled for
     * execution.
     */
    IGame getAutolockingGame();
}
