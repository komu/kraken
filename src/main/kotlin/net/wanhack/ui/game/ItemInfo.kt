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

package net.wanhack.ui.game

import net.wanhack.model.item.Item

class ItemInfo(val item: Item, val inUse: Boolean): Comparable<ItemInfo> {

    val title: String
        get() = item.title

    val description: String
        get() = item.description

    val letter: Char
        get() = item.letter

    override fun compareTo(other: ItemInfo) =
        if (inUse != other.inUse)
            if (inUse) -1 else 1
        else
            title.compareTo(other.title)
}
