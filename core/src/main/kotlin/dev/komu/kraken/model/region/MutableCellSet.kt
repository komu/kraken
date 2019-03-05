/*
 * Copyright 2013 The Releasers of Kraken
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

package dev.komu.kraken.model.region

import dev.komu.kraken.utils.randomInt
import java.util.*

class MutableCellSet(val region: Region): AbstractSet<Cell>(), CellSet {

    private val cells = BitSet(region.width * region.height)

    private fun index(x: Int, y: Int): Int {
        assert(x >= 0 && x < region.width) { "x out of bounds: $x" }
        assert(y >= 0 && y < region.height)  { "y out of bounds: $y" }
        return x + y * region.width
    }

    override fun copy(): MutableCellSet {
        val result = MutableCellSet(region)
        result.addAll(this)
        return result
    }

    override fun randomElement(): Cell =
        this[randomInt(size)]

    private fun point(index: Int): Cell =
        region[index % region.width, index / region.width]

    operator fun get(index: Int): Cell {
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

    override fun add(e: Cell) = add(e.coordinate)

    fun add(c: Coordinate) = add(c.x, c.y)

    fun add(x: Int, y: Int): Boolean {
        val index = index(x, y)
        val old = cells.get(index)
        cells.set(index)
        return !old
    }

    override fun remove(element: Cell) = remove(element.coordinate.x, element.coordinate.y)

    fun remove(x: Int, y: Int): Boolean {
        val index = index(x, y)
        val old = cells.get(index)
        cells.clear(index)
        return old
    }

    override fun clear() {
        cells.clear()
    }

    override fun contains(element: Cell) = contains(element.coordinate.x, element.coordinate.y)

    override fun contains(x: Int, y: Int) =
        x >= 0 && x < region.width && y >= 0 && y < region.height && cells.get(index(x, y))

    override val size: Int
        get() = cells.cardinality()

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
