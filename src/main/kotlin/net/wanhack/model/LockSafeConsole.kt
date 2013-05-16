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
import net.wanhack.model.item.Item

class LockSafeConsole(val console: Console, val gameRef: GameRef): Console {

    override fun message(message: String) {
        console.message(message)
    }

    override fun ask(question: String) =
        gameRef.withoutLock { console.ask(question) }

    override fun selectDirection() =
        gameRef.withoutLock { console.selectDirection() }

    override fun <T: Item> selectItem(itemType: Class<T>, message: String, items: Collection<T>) =
        gameRef.withoutLock { console.selectItem(itemType, message, items) }

    override fun selectItems(message: String, items: Collection<Item>) =
        gameRef.withoutLock { console.selectItems(message, items) }
}
