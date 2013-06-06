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

package net.wanhack.utils.collections

import java.util.AbstractCollection
import java.util.ArrayList
import java.util.ConcurrentModificationException

class CircularBuffer<E : Any>(capacity: Int): AbstractCollection<E>() {

    private val buffer = Array<E?>(capacity) { null }
    private var start = 0
    private var size = 0
    private var modCount = 0;

    {
        require(capacity > 0)
    }

    override fun add(e: E): Boolean {
        modCount++
        if (size < capacity) {
            buffer[size] = e
            size++
        } else {
            buffer[start] = e
            start = (start + 1) % buffer.size
        }
        return true
    }

    fun get(index: Int): E =
        buffer[index(index)]!!

    private fun set(index: Int, value: E) {
        modCount++
        buffer[index(index)] = value
    }

    private fun index(index: Int): Int {
        if (index < 0 || index >= size)
            throw IndexOutOfBoundsException("size=$size, index=$index")

        return (start + index) % buffer.size
    }

    fun last(): E {
        if (isEmpty())
            throw IllegalStateException("buffer is empty")

        return get(size - 1)
    }

    fun last(count: Int): List<E> {
        if (count < 0)
            throw IllegalArgumentException("negative count")

        val n = Math.min(count, size)
        val result = ArrayList<E>(n)
        for (i in 0..n - 1)
            result.add(get(size - n + i))

        return result
    }

    fun replaceLast(value: E) {
        if (isEmpty())
            throw IllegalStateException("buffer is empty")

        modCount++
        set(size - 1, value)
    }

    override fun clear() {
        size = 0
        start = 0
        modCount++
        for (i in buffer.indices)
            buffer[i] = null
    }

    override fun isEmpty() = size == 0

    override fun size() = size

    val capacity: Int
        get() = buffer.size

    override fun iterator() = BufferIterator()

    override fun equals(other: Any?): Boolean {
        if (other == this)
            return true

        if (other is CircularBuffer<*>) {
            if (size == other.size && buffer.size == other.buffer.size) {
                for (i in indices)
                    if (this[i] != other[i])
                        return false
                return true
            } else
                return false
        }

        return false
    }

    override fun hashCode(): Int {
        var hash = 0
        for (i in this.indices)
            hash = hash * 79 + this[i].hashCode()

        return hash
    }

    private inner class BufferIterator: MutableIterator<E> {
        private var index: Int = 0
        private val expectedModCount = modCount

        override fun hasNext() = index < size

        override fun next(): E {
            if (modCount != expectedModCount)
                throw ConcurrentModificationException()

            return get(index++)
        }

        override fun remove() =
            throw UnsupportedOperationException("remove not supported")
    }
}
