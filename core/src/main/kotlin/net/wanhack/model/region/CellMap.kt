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

package net.wanhack.model.region

import java.util.AbstractMap
import java.util.AbstractSet
import java.util.NoSuchElementException
import java.util.Objects

@Suppress("UNCHECKED_CAST")
class CellMap<V : Any>(private val region: Region): AbstractMap<Cell, V>() {

    private val mappings = Array<Any?>(region.width * region.height) { null }

    override fun get(key: Cell?): V? =
        mappings[index((key as Cell).coordinate)] as V?

    override fun put(key: Cell, value: V): V? {
        val index = index(key.coordinate)
        val old = mappings[index]
        mappings[index] = value
        return old as V?
    }

    override fun remove(key: Cell?): V? =
        if (key is Cell) {
            val index = index(key.coordinate)
            val old = mappings[index]
            mappings[index] = null
            old as V?
        } else
            null

    override fun isEmpty() = size != 0

    override val size: Int
        get() = mappings.count { it != null }

    override fun clear() {
        for (i in mappings.indices)
            mappings[i] = null
    }

    override val entries: MutableSet<MutableMap.MutableEntry<Cell, V>>
        get() = EntrySet(this)

    private fun index(c: Coordinate): Int =
        c.x + c.y * region.width

    private fun cell(index: Int): Cell =
        region[index % region.width, index / region.width]

    private class EntrySet<V : Any>(val map: CellMap<V>): AbstractSet<MutableMap.MutableEntry<Cell, V>>() {

        override fun iterator() =
            EntryIterator(map)

        override fun add(element: MutableMap.MutableEntry<Cell, V>): Boolean {
            map[element.key] = element.value
            return true
        }

        override fun remove(element: MutableMap.MutableEntry<Cell, V>?): Boolean {
            if (element is Map.Entry<Any?, Any?>) {
                val key = element.key
                val value = map[key]
                if (value == element.value)
                    return map.remove(key) != null
                else
                    return false
            }

            return false
        }

        override fun contains(element: MutableMap.MutableEntry<Cell, V>?): Boolean =
            element != null && map[element.key] == element.value

        override val size: Int
            get() = map.size
    }

    private class CellMapEntry<V : Any>(val map: CellMap<V>, val index: Int): MutableMap.MutableEntry<Cell, V> {

        override val key: Cell
            get() = map.cell(index)

        override val value: V
            get() = map.mappings[index]!! as V

        override fun setValue(newValue: V): V {
            val old = map.mappings[index]
            map.mappings[index] = newValue
            return old!! as V
        }

        override fun equals(other: Any?): Boolean {
            if (other == this)
                return true

            return other is Map.Entry<Any?, Any?> && key == other.key && value == other.value
        }

        override fun hashCode() = Objects.hash(key, value)
    }

    private class EntryIterator<V : Any>(val map: CellMap<V>): MutableIterator<MutableMap.MutableEntry<Cell, V>> {
        private var index = 0
        private var previous = -1

        override fun hasNext(): Boolean {
            while (index < map.mappings.size && map.mappings[index] == null)
                index++

            return index < map.mappings.size
        }

        override fun next(): MutableMap.MutableEntry<Cell, V> {
            if (hasNext()) {
                previous = index++
                return CellMapEntry(map, previous)
            }
            else
                throw NoSuchElementException()
        }

        override fun remove() {
            if (previous == -1)
                throw IllegalStateException()
            map.mappings[previous] = null
        }
    }
}
