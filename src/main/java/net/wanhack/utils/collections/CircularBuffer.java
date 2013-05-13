/*
 *  Copyright 2005 The Wanhack Team
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.wanhack.utils.collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

/**
 * A circular buffer: works like normal list until the capacity of the
 * buffer is full in which case the oldest items are removed to make
 * room for new ones.
 * <p>
 * The add, get and size operations are all O(1).
 */
public final class CircularBuffer<E>
        extends AbstractCollection<E> implements Serializable {

    private transient E[] buffer;
    private transient int start = 0;
    private transient int size = 0;
    private transient int modCount = 0;
    private static final long serialVersionUID = 0;
    
    /**
     * Constructs new buffer with given capacity.
     * 
     * @param capacity The maximum capacity of this buffer
     * @throws IllegalArgumentException if capacity is not positive
     */
    @SuppressWarnings("unchecked")
    public CircularBuffer(int capacity) {
        if (capacity <= 0)
            throw new IllegalArgumentException("non-positive capacity: " + capacity);
        
        buffer = (E[]) new Object[capacity];
    }
    
    /**
     * Adds a new value to the end of this buffer. If the capacity of buffer
     * is exceeded after adding the item, the first item of this buffer is
     * removed.
     * 
     * @param value to add
     */
    @Override
    public boolean add(E value) {
        modCount++;
        if (size < capacity()) {
            buffer[size] = value;
            size++;
        } else {
            buffer[start] = value;
            start = (start + 1) % buffer.length;
        }
        return true;
    }
    
    /**
     * Returns the value of item at given index.
     * 
     * @param index of the value to get
     * @return Value
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    public E get(int index) {
        return buffer[index(index)];
    }
    
    /**
     * Sets the value at given index.
     */
    private void set(int index, E value) {
        modCount++;
        buffer[index(index)] = value;
    }
    
    /**
     * Returns the physical index of <code>buffer</code> array for given
     * logical index.
     */
    private int index(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException(
                "size=" + size + ", index=" + index);
        
        return (start + index) % buffer.length;
    }

    /**
     * Returns the last item of this buffer.
     * 
     * @return Last item of this buffer
     * @throws IllegalStateException if buffer is empty
     */
    public E last() {
        if (isEmpty()) throw new IllegalStateException("buffer is empty");
        
        return get(size - 1);
    }

    /**
     * Returns last <code>count</code> items of this buffer in addition
     * order. If buffer does contain <code>count</code> items, returns
     * all items of buffer.
     * 
     * @param count Number of items to get
     * @return List of size <code>min(count, size)</code>
     */
    public List<E> last(int count) {
        if (count < 0) throw new IllegalArgumentException("negative count");
        
        int n = Math.min(count, size);
        
        ArrayList<E> result = new ArrayList<E>(n);
        for (int i = 0; i < n ; i++) {
            result.add(get(size - n + i));
        }
        return result;
    }
    
    /**
     * Replaces the last item of this buffer with <code>value</code>
     * without changing the size of this buffer.
     * 
     * @param value to set as the last value
     * @throws IllegalStateException if buffer is empty
     */
    public void replaceLast(E value) {
        if (isEmpty()) throw new IllegalStateException("buffer is empty");
        
        modCount++;
        set(size - 1, value);
    }
    
    /**
     * Clears this buffer.
     */
    @Override
    public void clear() {
        size = 0;
        start = 0;
        modCount++;
        
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = null;
        }
    }

    /**
     * Returns true if this buffer is empty.
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the size of this buffer.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns the maximum capacity of this buffer.
     */
    public int capacity() {
        return buffer.length;
    }
    
    /**
     * @see java.util.AbstractCollection#iterator()
     */
    @Override
    public Iterator<E> iterator() {
        return new BufferIterator();
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        
        if (obj instanceof CircularBuffer) {
            CircularBuffer<?> rhs = (CircularBuffer<?>) obj;
            if (size == rhs.size && buffer.length == rhs.buffer.length) {
                for (int i = 0; i < size; i++) {
                    if (!equals(get(i), rhs.get(i))) {
                        return false;
                    }
                }
                
                return true;
                
            } else {
                return false;
            }
        }
        
        return false;
    }
    
    private static boolean equals(Object o1, Object o2) {
        return (o1 != null) ? o1.equals(o2) : (o2 == null);
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        
        for (int i = 0; i < size; i++) {
            E value = get(i);
            hash = hash * 79 + (value != null ? value.hashCode() : 0);
        }
        
        return hash;
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        
        out.writeInt(size);
        out.writeInt(buffer.length);
        
        for (int i = 0; i < size; i++) {
            out.writeObject(get(i));
        }
    }
    
    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        
        start = 0;
        size = in.readInt();
        
        int capacity = in.readInt();
        buffer = (E[]) new Object[capacity];
        
        for (int i = 0; i < size; i++) {
            buffer[i] = (E) in.readObject();
        }
    }

    /**
     * Iterator class for this buffer.
     */
    private class BufferIterator implements Iterator<E> {
        private int index = 0;
        private int expectedModCount = modCount;
        
        public boolean hasNext() {
            return index < size;
        }
        
        public E next() {
            checkForComodification();
            return get(index++);
        }
        
        private final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
        
        public void remove() {
            throw new UnsupportedOperationException("remove not supported");
        }
    }
}
