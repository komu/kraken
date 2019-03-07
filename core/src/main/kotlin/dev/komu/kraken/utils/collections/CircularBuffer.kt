package dev.komu.kraken.utils.collections

import java.util.*

class CircularBuffer<E : Any>(capacity: Int): AbstractCollection<E>() {

    private val buffer = Array<Any?>(capacity) { null }
    private var start = 0
    private var _size = 0
    private var modCount = 0

    init {
        require(capacity > 0)
    }

    override fun add(element: E): Boolean {
        modCount++
        if (_size < capacity) {
            buffer[_size] = element
            _size++
        } else {
            buffer[start] = element
            start = (start + 1) % buffer.size
        }
        return true
    }

    @Suppress("UNCHECKED_CAST")
    operator fun get(index: Int): E =
        buffer[index(index)]!! as E

    private fun set(index: Int, value: E) {
        modCount++
        buffer[index(index)] = value
    }

    private fun index(index: Int): Int {
        if (index < 0 || index >= _size)
            throw IndexOutOfBoundsException("size=$_size, index=$index")

        return (start + index) % buffer.size
    }

    fun last(): E {
        if (isEmpty())
            throw IllegalStateException("buffer is empty")

        return get(_size - 1)
    }

    fun last(count: Int): List<E> {
        if (count < 0)
            throw IllegalArgumentException("negative count")

        val n = count.coerceAtMost(_size)
        val result = ArrayList<E>(n)
        for (i in 0 until n)
            result.add(get(_size - n + i))

        return result
    }

    fun replaceLast(value: E) {
        if (isEmpty())
            throw IllegalStateException("buffer is empty")

        modCount++
        set(_size - 1, value)
    }

    override fun clear() {
        _size = 0
        start = 0
        modCount++
        for (i in buffer.indices)
            buffer[i] = null
    }

    override fun isEmpty() = _size == 0

    override val size: Int
        get() = _size

    val capacity: Int
        get() = buffer.size

    override fun iterator(): MutableIterator<E> = BufferIterator()

    override fun equals(other: Any?): Boolean {
        if (other == this)
            return true

        if (other is CircularBuffer<*>) {
            return if (_size == other._size && buffer.size == other.buffer.size) {
                indices.none { this[it] != other[it] }
            } else
                false
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

        override fun hasNext() = index < _size

        override fun next(): E {
            if (modCount != expectedModCount)
                throw ConcurrentModificationException()

            return get(index++)
        }

        override fun remove() =
            throw UnsupportedOperationException("remove not supported")
    }
}
