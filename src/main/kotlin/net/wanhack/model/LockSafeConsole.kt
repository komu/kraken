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

import net.wanhack.model.common.Console
import net.wanhack.model.common.Direction
import net.wanhack.model.item.Item

class LockSafeConsole(private val console: Console, private val gameRef: DefaultGameRef): Console {

    override fun message(message: String) {
        console.message(message)
    }

    override fun ask(question: String, vararg args: Any?): Boolean {
        gameRef.unlockWriteLock()
        try {
            return console.ask(question, *args)
        } finally {
            gameRef.lockWriteLock()
        }
    }

    override fun selectDirection(): Direction? {
        gameRef.unlockWriteLock()
        try {
            return console.selectDirection()
        } finally {
            gameRef.lockWriteLock()
        }
    }

    override fun <T: Item> selectItem(itemType: Class<T>, message: String, items: Collection<T>): T? {
        gameRef.unlockWriteLock()
        try {
            return console.selectItem(itemType, message, items)
        } finally {
            gameRef.lockWriteLock()
        }
    }

    override fun selectItems(message: String, items: Collection<Item>): Set<Item> {
        gameRef.unlockWriteLock()
        try {
            return console.selectItems(message, items)
        } finally {
            gameRef.lockWriteLock()
        }
    }
}
