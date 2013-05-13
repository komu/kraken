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

import java.util.Iterator;

/**
 * Abstract base class for iterators that only implement one method,
 * {@link #nextOrNull()}, which returns the next element or null if
 * end has been reached. This makes it simpler to implement certain
 * types of iterators.
 */
public abstract class AbstractSimpleIterator<T> implements Iterator<T> {

    private T next = null;
    private boolean end = false;
    
    public final T next() {
        update();
        
        if (!end) {
            T value = next;
            next = null;
            return value;
        } else {
            throw new IllegalStateException("iteration at end");
        }
    }
    
    public final boolean hasNext() {
        update();
        
        return !end;
    }
    
    /**
     * Returns the next element in the sequence, or null if end has
     * been reached.
     */
    protected abstract T nextOrNull();
    
    private void update() {
        if (end) return;
        
        if (next == null) {
            next = nextOrNull();
            
            if (next == null) {
                end = true;
            }
        }
    }
    
    public final void remove() {
        throw new UnsupportedOperationException("remove is not supported");
    }
}
