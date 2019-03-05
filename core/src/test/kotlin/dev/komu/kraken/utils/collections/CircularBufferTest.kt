package dev.komu.kraken.utils.collections

import org.junit.Assert.assertEquals
import org.junit.Test

class CircularBufferTest {

    @Test
    fun empty() {
        val buffer = CircularBuffer<String>(2)

        assertEquals("capacity", 2, buffer.capacity)
        assertEquals("size", 0, buffer.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun constructWithZeroSize() {
        CircularBuffer<String>(0)
    }

    @Test
    fun addWithoutOverflow() {
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

    @Test
    fun addOverflowing() {
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

    @Test
    fun lastN() {
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
