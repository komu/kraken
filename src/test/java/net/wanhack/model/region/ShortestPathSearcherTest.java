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

import java.util.List;

import net.wanhack.model.region.Cell;
import net.wanhack.model.region.CellType;
import net.wanhack.model.region.Region;
import net.wanhack.model.region.ShortestPathSearcher;
import net.wanhack.model.region.World;

import junit.framework.TestCase;

public class ShortestPathSearcherTest extends TestCase {

    public void testPathOnEmptyRegion() {
        List<Cell> path = searcher(0, 0, 4, 4,
                "#####",
                "#####",
                "#####",
                "#####",
                "#####");
        
        assertPath(path, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4);
    }

    public void testPathOnSimpleRegion() {
        List<Cell> path = searcher(0, 0, 4, 4,
                "#####",
                "#####",
                "## ##",
                "#####",
                "#####");
        
        assertPath(path, 0, 0,   1, 1,   2, 1,   3, 2,   4, 3,   4, 4);
    }

    public void testPathOnSimpleRegion2() {
        List<Cell> path = searcher(0, 0, 4, 4,
                "#####",
                "##  #",
                "## ##",
                "## # ",
                "## ##");
        
        assertPath(path, 
                   0, 0,
                   1, 1,
                   2, 0,
                   3, 0,
                   4, 1,
                   4, 2,
                   3, 3,
                   4, 4);
    }

    public void testPathWhenNoPathFound() {
        List<Cell> path = searcher(0, 0, 4, 4,
                "## ##",
                "## ##",
                "## ##",
                "## ##",
                "## ##");
        
        assertNull(path);
    }

    private static void assertPath(List<Cell> path, int... expected) {
        assertTrue("invalid expected path", expected.length % 2 == 0);
        assertEquals("length", expected.length / 2, path.size());
        
        for (int i = 0; i < path.size(); i++) {
            assertPoint(expected[2*i], expected[2*i+1], path.get(i));
        }
    }
    
    private static void assertPoint(int x, int y, Cell cell) {
        if (x != cell.x || y != cell.y) {
            fail("Expected (" + x + "," + y + "), was (" + cell.x + "," + cell.y + ")");
        }
    }
    
    private static List<Cell> searcher(int x1, int y1, int x2, int y2, String... lines) {
        Region region = parse(lines);
        Cell start = region.getCell(x1, y1);
        Cell goal = region.getCell(x2, y2);
        return new ShortestPathSearcher(parse(lines)).findShortestPath(start, goal);
    }
    
    private static Region parse(String... lines) {
        int width = 0;
        for (String line : lines) {
            width = Math.max(width, line.length());
        }
        
        Region region = new Region(new World(), "test", 1, width, lines.length);
        
        for (int y = 0; y < lines.length; y++) {
            for (int x = 0; x < lines[y].length(); x++) {
                if (lines[y].charAt(x) == '#') {
                    region.getCell(x, y).setType(CellType.HALLWAY_FLOOR);
                }
            }
        }
        
        return region;
    }
}
