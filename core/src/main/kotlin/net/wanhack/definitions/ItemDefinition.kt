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

package net.wanhack.definitions

import net.wanhack.model.item.Item
import java.util.ArrayList

class ItemDefinition<T : Item>(val name: String, val createItem: () -> T) : ObjectDefinition<T>() {

    override var level: Int? = null

    var createdInstances = 0
    var maximumInstances = Integer.MAX_VALUE
    private val initHooks = ArrayList<T.() -> Unit>()

    val instantiable: Boolean
        get() = createdInstances < maximumInstances

    override fun create(): T {
        val obj = createItem()

        for (hook in initHooks)
            obj.hook()

        createdInstances++
        return obj
    }

    fun init(hook: T.() -> Unit): ItemDefinition<T> {
        initHooks.add(hook)
        return this
    }

    fun toString() = "ItemDefinition [name=$name]"
}
