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

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import net.wanhack.utils.Predicates;

public class CellTest extends TestCase {

    private Region region;
    
    @Override
    protected void setUp() throws Exception {
        region = new Region(new World(), "foo", 4, 10, 10);
    }
    
    @Override
    protected void tearDown() throws Exception {
        region = null;
    }
    
    public void testPointsBetweenEmpty() {
        assertPointsBetween("[]",   5, 5,   5, 5);
        assertPointsBetween("[]",   5, 5,   5, 6);
        assertPointsBetween("[]",   5, 5,   6, 5);
        assertPointsBetween("[]",   5, 5,   6, 6);
    }
    
    public void testPointsBetweenSimple() {
        assertPointsBetween("[(5,6)]",    5, 5,    5, 7);
        assertPointsBetween("[(5,4)]",    5, 5,    5, 3);
        assertPointsBetween("[(6,5)]",    5, 5,    7, 5);
        assertPointsBetween("[(4,5)]",    5, 5,    3, 5);
    }

    public void testPointsBetweenSimpleLonger() {
        assertPointsBetween("[(5,6), (5,7)]",    5, 5,    5, 8);
        assertPointsBetween("[(5,4), (5,3)]",    5, 5,    5, 2);
        assertPointsBetween("[(6,5), (7,5)]",    5, 5,    8, 5);
        assertPointsBetween("[(4,5), (3,5)]",    5, 5,    2, 5);
    }
    
    public void testPointsBetween45Degrees() {
        assertPointsBetween("[(4,4), (3,3)]",    5, 5,    2, 2);
        assertPointsBetween("[(6,6), (7,7)]",    5, 5,    8, 8);
        assertPointsBetween("[(4,6), (3,7)]",    5, 5,    2, 8);
        assertPointsBetween("[(6,4), (7,3)]",    5, 5,    8, 2);
    }
    
    public void testGetCellsAtDistanceZero() {
        List<Cell> cells = getCellsAtDistance(5, 5, 0);
        
        assertList(cells, 
                   region.getCell(5, 5));
    }

    public void testGetCellsAtDistanceOne() {
        List<Cell> cells = getCellsAtDistance(5, 5, 1);
        
        assertList(cells,
                   region.getCell(4, 4),
                   region.getCell(5, 4),
                   region.getCell(6, 4),
                   region.getCell(4, 5),
                   region.getCell(6, 5),
                   region.getCell(4, 6),
                   region.getCell(5, 6),
                   region.getCell(6, 6));
    }

    public void testGetCellsAtDistanceTwo() {
        List<Cell> cells = getCellsAtDistance(5, 5, 2);
        
        assertList(cells,
                   region.getCell(3, 3),
                   region.getCell(4, 3),
                   region.getCell(5, 3),
                   region.getCell(6, 3),
                   region.getCell(7, 3),
                   
                   region.getCell(3, 4),
                   region.getCell(7, 4),
                   region.getCell(3, 5),
                   region.getCell(7, 5),
                   region.getCell(3, 6),
                   region.getCell(7, 6),
                   
                   region.getCell(3, 7),
                   region.getCell(4, 7),
                   region.getCell(5, 7),
                   region.getCell(6, 7),
                   region.getCell(7, 7));
    }
    
    private List<Cell> getCellsAtDistance(int x, int y, int d) {
        Cell cell = region.getCell(x, y);
        return cell.getMatchingCellsAtDistance(d, Predicates.<Cell>matchAlways());
    }

    private void assertPointsBetween(String repr, int x1, int y1, int x2, int y2) {
        Cell c1 = region.getCell(x1, y1);
        Cell c2 = region.getCell(x2, y2);
        
        assertEquals(repr, c1.getCellsBetween(c2).toString());
    }
    
    private static <T> void assertList(List<T> got, T... expected) {
        assertEquals(Arrays.asList(expected), got);
    }
}
