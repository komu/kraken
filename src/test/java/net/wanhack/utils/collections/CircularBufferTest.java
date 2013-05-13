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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import net.wanhack.utils.collections.CircularBuffer;

import junit.framework.TestCase;


public class CircularBufferTest extends TestCase {

    public void testEmpty() {
        CircularBuffer<String> buffer = new CircularBuffer<String>(2);
        
        assertEquals("capacity", 2, buffer.capacity());
        assertEquals("size", 0, buffer.size());
    }

    public void testConstructWithZeroSize() {
        try {
            new CircularBuffer<String>(0);
            fail("expected exception");
        } catch (IllegalArgumentException e) {
        }
    }
    
    public void testAddWithoutOverflow() {
        CircularBuffer<String> buffer = new CircularBuffer<String>(2);
        
        assertEquals("size", 0, buffer.size());
        assertEquals("[]", buffer.toString());
        
        buffer.add("foo");
        assertEquals("size", 1, buffer.size());
        assertEquals("[foo]", buffer.toString());
        
        buffer.add("bar");
        assertEquals("size", 2, buffer.size());
        assertEquals("[foo, bar]", buffer.toString());
    }

    public void testAddOverflowing() {
        CircularBuffer<String> buffer = new CircularBuffer<String>(2);
        
        assertEquals("size", 0, buffer.size());
        assertEquals("[]", buffer.toString());
        
        buffer.add("foo");
        assertEquals("size", 1, buffer.size());
        assertEquals("[foo]", buffer.toString());
        
        buffer.add("bar");
        assertEquals("size", 2, buffer.size());
        assertEquals("[foo, bar]", buffer.toString());
        
        buffer.add("baz");
        assertEquals("size", 2, buffer.size());
        assertEquals("[bar, baz]", buffer.toString());
        
        buffer.add("bad");
        assertEquals("size", 2, buffer.size());
        assertEquals("[baz, bad]", buffer.toString());
        
        buffer.add("xyzzy");
        assertEquals("size", 2, buffer.size());
        assertEquals("[bad, xyzzy]", buffer.toString());
    }

    public void testLastN() {
        CircularBuffer<String> buffer = new CircularBuffer<String>(3);

        buffer.add("1");
        buffer.add("2");
        buffer.add("3");
        buffer.add("4");
        
        List<String> lastTwo = buffer.last(2);
        assertEquals(2, lastTwo.size());
        assertEquals("3", lastTwo.get(0));
        assertEquals("4", lastTwo.get(1));
    }
    
    public void testSerialization() {
        CircularBuffer<String> buffer = new CircularBuffer<String>(3);
        
        assertSerializedEquals(buffer);

        buffer.add("1");
        assertSerializedEquals(buffer);
        
        buffer.add("2");
        assertSerializedEquals(buffer);
        
        buffer.add("3");
        assertSerializedEquals(buffer);
        
        buffer.add("4");
        assertSerializedEquals(buffer);
    }
    
    private static void assertSerializedEquals(Serializable value) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(value);
            oos.close();
            
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object readValue = ois.readObject();
            
            assertEquals(value, readValue);
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
