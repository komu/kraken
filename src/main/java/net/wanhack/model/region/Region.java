/*
 *  Copyright 2005-2006 The Wanhack Team
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.wanhack.model.creature.Creature;
import net.wanhack.model.creature.Player;
import net.wanhack.model.item.Item;
import net.wanhack.utils.Predicate;

/**
 * Region is a small area of the world that is active at the time.
 * Region consists of multiple {@link Cell}s.
 */
public final class Region implements Iterable<Cell>, Serializable {

    public static final int DEFAULT_REGION_WIDTH = 80;
    public static final int DEFAULT_REGION_HEIGHT = 25;

    private final World world;
    private final String name;
    private final int level;
    private final int width;
    private final int height;
    private final Cell[] cells;
    private final Map<String, Cell> startCells = new HashMap<String, Cell>();
    private static final long serialVersionUID = 0;
    
    public Region(World world, String name, int level, int width, int height) {
        this.world = world;
        this.name = name;
        this.level = level;
        this.width = width;
        this.height = height;
        this.cells = new Cell[width * height];
        
        // Create cell for every point of the region
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                CellState state = new DefaultCellState(CellType.WALL);
                cells[x + y * width] = new Cell(this, x, y, state);
            }
        }
        
        // Add undiggable walls for borders
        for (int x = 0; x < width; x++) {
            getCell(x, 0).setType(CellType.UNDIGGABLE_WALL);
            getCell(x, height - 1).setType(CellType.UNDIGGABLE_WALL);
        }
        
        for (int y = 0; y < height; y++) {
            getCell(0, y).setType(CellType.UNDIGGABLE_WALL);
            getCell(width - 1, y).setType(CellType.UNDIGGABLE_WALL);
        }
    }
    
    public World getWorld() {
        return world;
    }
    
    public void reveal() {
        for (Cell cell : this) {
            cell.setSeen();
        }
    }
    
    public String getTitle() {
        return name;
    }
    
    public int getLevel() {
        return level;
    }
    
    public Iterator<Cell> iterator() {
        return Arrays.asList(cells).iterator();
    }
    
    public void setPlayerLocation(Player player, String location) {
        Cell startCell = startCells.get(location);
        if (startCell == null) 
            throw new IllegalStateException(
                    "Region '" + name + "' has no start point named '" + location + "'.");
        
        player.setCell(startCell);
    }
    
    public List<Creature> getCreatures() {
        List<Creature> creatures = new ArrayList<Creature>();
        
        for (Cell cell : cells) {
            if (cell.getCreature() != null) {
                creatures.add(cell.getCreature());
            }
        }
        
        return creatures;
    }
    
    public List<Cell> findPath(Cell start, Cell goal) {
        return new ShortestPathSearcher(this).findShortestPath(start, goal);
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }

    public Cell getCell(int x, int y) {
        assert containsPoint(x, y) : "out of bounds: (" + x + ", " + y + ")";
        
        return cells[x + y * width];
    }

    public Cell getCellOrNull(int x, int y) {
        int index = x + y * width;
        
        if (index >= 0 && index < cells.length) {
            return cells[index];
        } else {
            return null;
        }
    }
    
    public boolean containsPoint(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    
    public void updateSeenCells(Set<Cell> seen) {
        for (int i = 0; i < cells.length; i++) {
            if (seen.contains(cells[i])) {
                cells[i].setSeen();
            }
        }
    }

    public void addPortal(int x, int y, String target, String location, boolean up) {
        getCell(x, y).setPortal(new Portal(target, location, up));
    }
    
    public void addStartPoint(String name, int x, int y) {
        Cell old = startCells.put(name, getCell(x, y));
        if (old != null) {
            throw new IllegalStateException(
                    "Tried to define start point '" + name + 
                    "' multiple tiles for region '" + name + "'."); 
        }
    }
    
    public void addCreature(Creature creature, int x, int y) {
        creature.setCell(getCell(x, y));
    }
    
    public void addItem(int x, int y, Item item) {
        getCell(x, y).addItem(item);
    }

    public CellSet getCells() {
        CellSet result = new CellSet(this);
        
        for (int i = 0; i < cells.length; i++) {
            result.add(cells[i]);
        }
        
        return result;
    }

    public CellSet getCellsForItemsAndCreatures() {
        return getMatchingCells(CellPredicates.FLOOR);
    }
    
    public CellSet getRoomFloorCells() {
        return getMatchingCells(CellPredicates.IN_ROOM);
    }
    
    public CellSet getMatchingCells(Predicate<Cell> predicate) {
        CellSet result = new CellSet(this);
        
        for (int i = 0; i < cells.length; i++) {
            if (predicate.evalute(cells[i])) {
                result.add(cells[i]);
            }
        }
        
        return result; 
    }
    
    public void updateLighting() {
        for (int i = 0; i < cells.length; i++) {
            cells[i].resetLighting();
        }
        
        for (int i = 0; i < cells.length; i++) {
            cells[i].updateLighting();
        }
    }
}
