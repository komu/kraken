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

import java.util.Collection;
import java.util.Set;

import net.wanhack.model.common.Console;
import net.wanhack.model.common.Direction;
import net.wanhack.model.item.Item;


/**
 * Implementation of {@link Console} that releases the write-lock
 * before writing anything to the console, thus making sure that
 * the game does not deadlock.
 */
final class LockSafeConsole implements Console {

    private final Console console;
    private final DefaultGameRef gameRef;
    
    public LockSafeConsole(Console console, DefaultGameRef gameRef) {
        this.console = console;
        this.gameRef = gameRef;
    }

    public void message(String message) {
        console.message(message);
    }
    
    public boolean ask(String question, Object... args) {
        gameRef.unlockWriteLock();
        try {
            return console.ask(question, args);
        } finally {
            gameRef.lockWriteLock();
        }
    }

    public Direction selectDirection() {
        gameRef.unlockWriteLock();
        try {
            return console.selectDirection();
        } finally {
            gameRef.lockWriteLock();
        }
    }
    
    public <T extends Item> T selectItem(Class<T> type, 
                                         String message, 
                                         Collection< ? extends T> items) {
        gameRef.unlockWriteLock();
        try {
            return console.selectItem(type, message, items);
        } finally {
            gameRef.lockWriteLock();
        }
    }
    
    public Set<Item> selectItems(String message, Collection<? extends Item> items) {
        gameRef.unlockWriteLock();
        try {
            return console.selectItems(message, items);
        } finally {
            gameRef.lockWriteLock();
        }
    }
}
