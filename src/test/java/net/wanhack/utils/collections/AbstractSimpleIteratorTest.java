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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.wanhack.utils.collections.AbstractSimpleIterator;
import net.wanhack.utils.collections.IteratorIterable;

import junit.framework.TestCase;


public class AbstractSimpleIteratorTest extends TestCase {

    public void testThree() {
        List<String> result = itemsUsingTestIterator("foo", "bar", "baz");
        
        assertEquals("size", 3, result.size());
        assertEquals("foo", result.get(0));
        assertEquals("bar", result.get(1));
        assertEquals("baz", result.get(2));
    }
    
    public void testZero() {
        List<String> result = itemsUsingTestIterator();
        assertEquals(Collections.emptyList(), result);
    }
    
    private static List<String> itemsUsingTestIterator(final String... str) {
        List<String> result = new ArrayList<String>();
        
        for (String s : new IteratorIterable<String>(new TestIterator(str))) {
            result.add(s);
        }
        
        return result;
    }
    
    private static class TestIterator extends AbstractSimpleIterator<String> {
        
        private final String[] words;
        private int index = 0;
        
        public TestIterator(String[] words) {
            this.words = words;
        }
        
        @Override
        protected String nextOrNull() {
            if (index < words.length) {
                return words[index++];
            } else {
                return null;
            }
        }
    }
}
