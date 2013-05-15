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

import java.util.AbstractSet
import java.util.BitSet
import java.util.NoSuchElementException
import net.wanhack.utils.RandomUtils

class CellSet(val region: Region): AbstractSet<Cell>() {

    private val cells = BitSet(region.width * region.height)

    private fun index(x: Int, y: Int): Int {
        assert(x >= 0 && x < region.width) { "x out of bounds: $x" }
        assert(y >= 0 && y < region.height)  { "y out of bounds: $y" }
        return x + y * region.width
    }

    fun randomElement(): Cell =
        this[RandomUtils.randomInt(size)]

    private fun point(index: Int): Cell =
        region.getCell(index % region.width, index / region.width)

    fun get(index: Int): Cell {
        var i = index

        require(i >= 0)

        var bit = cells.nextSetBit(0)
        while (bit >= 0) {
            if (i-- == 0)
                return point(bit)

            bit = cells.nextSetBit(bit + 1)
        }

        throw IllegalArgumentException("no such index: $index")
    }

    override fun add(e: Cell) = add(e.x, e.y)

    fun add(x: Int, y: Int): Boolean {
        val index = index(x, y)
        val old = cells.get(index)
        cells.set(index)
        return !old
    }

    override fun remove(o: Any?) = o is Cell && remove(o.x, o.y)

    fun remove(x: Int, y: Int): Boolean {
        val index = index(x, y)
        val old = cells.get(index)
        cells.clear(index)
        return old
    }

    override fun clear() {
        cells.clear()
    }

    override fun contains(o: Any?) = o is Cell && contains(o.x, o.y)

    fun contains(x: Int, y: Int) =
        x >= 0 && x < region.width && y >= 0 && y < region.height && cells.get(index(x, y))

    override fun size() = cells.cardinality()

    override fun iterator(): MutableIterator<Cell> = MyIterator()

    private inner class MyIterator: MutableIterator<Cell> {
        private var index = cells.nextSetBit(0)

        override fun hasNext() = index >= 0

        override fun next(): Cell =
            if (index >= 0) {
                val point = point(index)
                index = cells.nextSetBit(index + 1)
                point
            } else
                throw NoSuchElementException()

        override fun remove() = throw UnsupportedOperationException()
    }
}
