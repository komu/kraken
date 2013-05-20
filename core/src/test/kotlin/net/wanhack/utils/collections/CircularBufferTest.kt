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

import org.junit.Test as test
import org.junit.Assert.*

class CircularBufferTest {
    
    test fun empty() {
        val buffer = CircularBuffer<String>(2)

        assertEquals("capacity", 2, buffer.capacity)
        assertEquals("size", 0, buffer.size)
    }

    test(expected=javaClass<IllegalArgumentException>())
    fun constructWithZeroSize() {
        CircularBuffer<String>(0)
    }
    
    test fun addWithoutOverflow() {
        val buffer = CircularBuffer<String>(2)
        
        assertEquals("size", 0, buffer.size)
        assertEquals("[]", buffer.toString())
        
        buffer.add("foo")
        
        assertEquals("size", 1, buffer.size)
        assertEquals("[foo]", buffer.toString())
        
        buffer.add("bar")
        
        assertEquals("size", 2, buffer.size)
        assertEquals("[foo, bar]", buffer.toString())
    }
    
    test fun addOverflowing() {
        val buffer = CircularBuffer<String>(2)
        
        assertEquals("size", 0, buffer.size)
        assertEquals("[]", buffer.toString())

        buffer.add("foo")
        
        assertEquals("size", 1, buffer.size)
        assertEquals("[foo]", buffer.toString())

        buffer.add("bar")
        
        assertEquals("size", 2, buffer.size)
        assertEquals("[foo, bar]", buffer.toString())

        buffer.add("baz")
        
        assertEquals("size", 2, buffer.size)
        assertEquals("[bar, baz]", buffer.toString())

        buffer.add("bad")
        
        assertEquals("size", 2, buffer.size)
        assertEquals("[baz, bad]", buffer.toString())
        
        buffer.add("xyzzy")
        
        assertEquals("size", 2, buffer.size)
        assertEquals("[bad, xyzzy]", buffer.toString())
    }

    test fun lastN() {
        val buffer = CircularBuffer<String>(3)
        buffer.add("1")
        buffer.add("2")
        buffer.add("3")
        buffer.add("4")

        val lastTwo = buffer.last(2)
        assertEquals(2, lastTwo.size)
        assertEquals("3", lastTwo[0])
        assertEquals("4", lastTwo[1])
    }
}
