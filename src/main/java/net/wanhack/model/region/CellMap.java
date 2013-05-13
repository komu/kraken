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
package net.wanhack.model.region;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import net.wanhack.utils.ObjectUtils;

/**
 * Map from {@link Cell} objects to values.
 */
public final class CellMap<V> 
        extends AbstractMap<Cell, V> implements Serializable {

    private final Region region;
    private final V[] mappings;
    private static final long serialVersionUID = 0;
    
    @SuppressWarnings("unchecked")
    public CellMap(Region region) {
        this.region = region;
        this.mappings = (V[]) new Object[region.getWidth() * region.getHeight()];
    }
    
    @Override
    public V get(Object key) {
        return mappings[index((Cell) key)];
    }
    
    @Override
    public V put(Cell key, V value) {
        int index = index(key);
        V old = mappings[index];
        mappings[index] = value;
        return old;
    }
    
    @Override
    public V remove(Object key) {
        if (key instanceof Cell) {
            return put((Cell) key, null);
        } else {
            return null;
        }
    }
    
    @Override
    public boolean isEmpty() {
        return size() != 0;
    }
    
    @Override
    public int size() {
        int size = 0;
        for (int i = 0; i < mappings.length; i++) {
            if (mappings[i] != null) {
                size++;
            }
        }
        return size;
    }

    @Override
    public void clear() {
        for (int i = 0; i < mappings.length; i++) {
            mappings[i] = null;
        }
    }
    
    @Override
    public Set<Entry<Cell, V>> entrySet() {
        return new EntrySet();
    }
    
    private int index(Cell cell) {
        return cell.x + cell.y * region.getWidth();
    }
    
    private Cell cell(int index) {
        int y = index / region.getWidth();
        int x = index % region.getWidth();
        
        return region.getCell(x, y);
    }
    
    private class EntrySet extends AbstractSet<Entry<Cell, V>> {
        @Override
        public Iterator<Entry<Cell, V>> iterator() {
            return new EntryIterator();
        }
        
        @Override
        public boolean add(Entry<Cell, V> o) {
            put(o.getKey(), o.getValue());
            return true;
        }
        
        @Override
        public boolean remove(Object o) {
            if (o instanceof Entry) {
                Entry entry = (Entry) o;
                
                Object value = get(entry.getKey());
                if (ObjectUtils.equals(value, entry.getValue())) {
                    Object old = CellMap.this.remove(entry.getKey());
                    return old != null;
                } else {
                    return false;
                }
            }
            return false;
        }
        
        @Override
        public boolean contains(Object o) {
            if (o instanceof Entry) {
                Entry entry = (Entry) o;
                
                Object value = get(entry.getKey());
                return ObjectUtils.equals(value, entry.getValue());
            }
            return false;
        }
        
        @Override
        public int size() {
            return CellMap.this.size();
        }
    }
    
    private class CellMapEntry implements Map.Entry<Cell, V> {
        private final int index;

        public CellMapEntry(int index) {
            this.index = index;
        }

        public Cell getKey() {
            return cell(index);
        }
        
        public V getValue() {
            return mappings[index];
        }
        
        public V setValue(V value) {
            V old = mappings[index];
            mappings[index] = value;
            return old;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            
            if (obj instanceof Map.Entry) {
                Map.Entry rhs = (Map.Entry) obj;
                
                return ObjectUtils.equals(getKey(), rhs.getKey())
                    && ObjectUtils.equals(getValue(), rhs.getValue());
            }

            return false;
        }
        
        @Override
        public int hashCode() {
            Cell key = getKey();
            V value = getValue();
            return key.hashCode() ^ (value == null ? 0 : value.hashCode());
        }
    }
    
    private class EntryIterator implements Iterator<Entry<Cell, V>> {
        private int index = 0;
        private int previous = -1;
        
        public boolean hasNext() {
            while (index < mappings.length && mappings[index] == null) {
                index++;
            }
            
            return index < mappings.length;
        }
        
        public Entry<Cell, V> next() {
            if (hasNext()) {
                previous = index++;
                return new CellMapEntry(previous);
            } else {
                throw new NoSuchElementException();
            }
        }
        
        public void remove() {
            if (previous != -1) {
                mappings[previous] = null;                            
            } else {
                throw new IllegalStateException();
            }
        }
    }
}
