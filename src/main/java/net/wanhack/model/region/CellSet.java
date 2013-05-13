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
import java.util.AbstractSet;
import java.util.BitSet;
import java.util.Iterator;

/**
 * A set of {@link Cell} objects on a region.
 */
public final class CellSet
        extends AbstractSet<Cell> implements Serializable {

    private final Region region;
    private final BitSet cells;
    protected static final long serialVersionUID = 0;
    
    public CellSet(Region region) {
        this.region = region;
        this.cells = new BitSet(region.getWidth() * region.getHeight());
    }
    
    public Region getRegion() {
        return region;
    }
    
    private int index(int x, int y) {
        assert x >= 0 && x < region.getWidth() : "x out of bounds: " + x;
        assert y >= 0 && y < region.getHeight() : "y out of bounds: " + y;
        
        return x + y * region.getWidth(); 
    }
    
    private Cell point(int index) {
        int x = index % region.getWidth();
        int y = index / region.getWidth();
        
        return region.getCell(x, y);
    }

    public Cell get(int index) {
        if (index < 0) throw new IllegalArgumentException("negative index");
        
        for(int i = cells.nextSetBit(0); i >= 0; i = cells.nextSetBit(i + 1)) {
            if (index-- == 0) {
                return point(i);
            }
        }
        throw new IllegalArgumentException("no such index: " + index);
    }
    
    @Override
    public boolean add(Cell c) {
        return add(c.x, c.y);
    }
    
    public boolean add(int x, int y) {
        int index = index(x, y);
        boolean old = cells.get(index);
        cells.set(index);
        return old == false;
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof Cell) {
            Cell cell = (Cell) o;
            return remove(cell.x, cell.y);
        } else {
            return false;
        }
    }

    public boolean remove(int x, int y) {
        int index = index(x, y);
        boolean old = cells.get(index);
        cells.clear(index);
        return old == true;
    }
    
    @Override
    public void clear() {
        cells.clear();
    }
    
    @Override
    public boolean contains(Object o) {
        if (o instanceof Cell) {
            Cell cell = (Cell) o;
            return contains(cell.x, cell.y);
        } else {
            return false;
        }
    }
    
    public boolean contains(int x, int y) {
        if (x >= 0 && x < region.getWidth() && y >= 0 && y < region.getHeight()) {
            return cells.get(index(x, y));
        } else {
            return false;
        }
    }
    
    @Override
    public int size() {
        return cells.cardinality();
    }
    
    @Override
    public Iterator<Cell> iterator() {
        return new MyIterator();
    }
    
    private class MyIterator implements Iterator<Cell> {
        private int index = cells.nextSetBit(0);
        
        public boolean hasNext() {
            return index >= 0;
        }
        
        public Cell next() {
            if (index >= 0) {
                Cell point = point(index);
                index = cells.nextSetBit(index + 1);
                return point;
            } else {
                throw new IllegalStateException();
            }
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
