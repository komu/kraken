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

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.sqrt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.wanhack.model.common.Direction;
import net.wanhack.model.creature.Creature;
import net.wanhack.model.creature.Player;
import net.wanhack.model.item.Item;
import net.wanhack.utils.NumberUtils;
import net.wanhack.utils.Predicate;
import net.wanhack.utils.collections.AbstractSimpleIterator;
import net.wanhack.utils.collections.IteratorIterable;


public final class Cell implements Serializable {

    public final int x;
    public final int y;
    private CellState state;
    private boolean hasBeenSeen = false;
    private final Region region;
    private final Set<Item> items = new HashSet<Item>();
    private Creature creature;
    private Portal portal;
    private int defaultLighting = 100;
    private int lighting = defaultLighting;
    
    /** Power of possible fixed light-source on this cell, or 0 for no light */
    private int lightPower = 0;
    
    private static final long serialVersionUID = 0;
    
    public Cell(Region region, int x, int y, CellState state) {
        this.region = region;
        this.state = state;
        this.x = x;
        this.y = y;
    }

    public void enter(Creature creature) {
        creature.setCell(this);
        
        if (state.getType().isStairs()) {
            creature.message("You see stairs here.");
        }
        
        Set<Item> items = this.getItems();
        if (items.size() == 1) {
            Item item = items.iterator().next();
            creature.message("You see here %s.", item.getTitle());
            
        } else if (items.size() > 1) {
            creature.message("You see multiple items here.");
        }        
    }
    
    public void openDoor(Creature opener) {
        if (state instanceof Door) {
            Door door = (Door) state;
            
            door.open(opener);
        }
    }
    
    public boolean closeDoor(Creature closer) {
        if (state instanceof Door) {
            Door door = (Door) state;
            
            if (door.isOpen()) {
                if (creature != null || !items.isEmpty()) {
                    closer.message("Something blocks the door.");
                    return false;
                }
                
                door.close(closer);
                return true;
            }
        }
        
        return false;
    }

    public Set<Item> getItems() {
        return items;
    }
    
    public Item getLargestItem() {
        int maxWeight = Integer.MIN_VALUE;
        Item largest = null;
        for (Item item : items) {
            if (item.getWeight() > maxWeight) {
                maxWeight = item.getWeight();
                largest = item;
            }
        }
        return largest;
    }
    
    public void addItem(Item item) {
        assert item != null : "null item";
        
        items.add(item);
    }
    
    public void addItems(Collection<? extends Item> items) {
        this.items.addAll(items);
    }
    
    public void removeItem(Item item) {
        assert item != null : "null item";
        
        items.remove(item);
    }

    public boolean isReachable(Cell goal) {
        return this == goal || region.findPath(this, goal) != null;
    }
    
    public Cell getCellTowards(Direction direction) {
        return region.getCell(x + direction.dx, y + direction.dy);
    }
    
    public void setCreature(Creature creature) {
        this.creature = creature;
    }
    
    public Creature getCreature() {
        return creature;
    }

    public JumpTarget getJumpTarget(boolean up) {
        if (portal != null) {
            return portal.getTarget(up);
        } else {
            return null;
        }
    }
    
    public Portal getPortal() {
        return portal;
    }
    
    public void setPortal(Portal portal) {
        this.portal = portal;
    }
    
    public void setLightPower(int lightPower) {
        this.lightPower = lightPower;
    }
    
    public boolean isFloor() {
        return state.getType().isFloor();
    }
    
    public boolean isInRoom() {
        return state.getType().isRoomFloor();
    }
    
    public boolean isClosedDoor() {
        return state.getType() == CellType.CLOSED_DOOR;
    }
    
    public boolean isAdjacent(Cell cell) {
        int dx = abs(x - cell.x);
        int dy = abs(y - cell.y);
        
        return cell != this && dx < 2 && dy < 2;
    }
    
    public boolean isRoomCorner() {
        if (countPassableMainNeighbours() != 2) {
            return false;
        }
        
        int previousPassable = 0;
        for (Direction d : Direction.values()) {
            Cell cell = getCellTowards(d);
            if (cell.isPassable()) {
                if (previousPassable == 2) {
                    return true;
                } else {
                    previousPassable++;
                }
            } else {
                previousPassable = 0;
            }
        }
        
        return false;
    }
    
    public Region getRegion() {
        return region;
    }
    
    public boolean search(Player player) {
        return state.search(player);
    }

    public void setState(CellState state) {
        this.state = state;
    }
    
    public CellType getType() {
        return state.getType();
    }
    
    public void setType(CellType type) {
        state = new DefaultCellState(type);
    }
    
    public boolean canDropItemToCell() {
        return state.getType().canDropItem();
    }

    public boolean canSeeThrough() {
        return state.getType().canSeeThrough();
    }
    
    public boolean isPassable() {
        return state.getType().isPassable();
    }
    
    public boolean canMoveInto(boolean corporeal) {
        if (creature != null) {
            return false;
        }
        
        return state.getType().canMoveInto(corporeal);
    }
    
    public boolean getHasBeenSeen() {
        return hasBeenSeen;
    }
    
    public void setSeen() {
        this.hasBeenSeen = true;
    }
    
    public int distance(Cell cell) {
        int dx = x - cell.x;
        int dy = y - cell.y;
        
        return (int) sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Returns true if this cell is "interesting", that is, if player
     * should stop to the cell when running.
     */
    public boolean isInteresting() {
        if (!state.getType().isFloor()) {
            return true;
        }
        
        if (!items.isEmpty()) {
            return true;
        }

        return false;
    }
    
    /**
     * Returns an iterable object that returns matching cells from region
     * in order that cells nearest to this cell are returned first. This
     * cell is not returned.
     */
    public Iterable<Cell> getMatchingCellsNearestFirst(
            final Predicate<Cell> predicate) {
        
        final int maxDistance = getMaximumDistanceFromEdgeOfRegion();
        
        return new IteratorIterable<Cell>(new AbstractSimpleIterator<Cell>() {
            private int distance = 0;
            private int pos = 0;
            private List<Cell> cellsAtCurrentDistance = Collections.emptyList();
            
            @Override
            protected Cell nextOrNull() {
                while (pos == cellsAtCurrentDistance.size()) {
                    if (distance >= maxDistance) {
                        return null;
                    }
                    
                    cellsAtCurrentDistance = 
                        getMatchingCellsAtDistance(++distance, predicate);
                    pos = 0;
                }
                
                return cellsAtCurrentDistance.get(pos++);
            }
        });
    }
    
    private int getMaximumDistanceFromEdgeOfRegion() {
        int maxX = max(x, region.getWidth() - x);
        int maxY = max(y, region.getHeight() - y);
        
        return max(maxX, maxY);
    }

    public List<Cell> getMatchingCellsAtDistance(int distance, 
                                                 Predicate<Cell> predicate) {
        if (distance == 0) {
            if (predicate.evalute(this)) {
                return Collections.singletonList(this);
            } else {
                return Collections.emptyList();
            }
        }
        
        int count = NumberUtils.cellsAtDistance(distance);
        ArrayList<Cell> cells = new ArrayList<Cell>(count);
        
        int x1 = x - distance;
        int y1 = y - distance;
        int x2 = x + distance;
        int y2 = y + distance;

        // Add the top row
        for (int xx = x1; xx <= x2; xx++) {
            Cell cell = region.getCellOrNull(xx, y1);
            if (cell != null && predicate.evalute(cell)) {
                cells.add(cell);
            }
        }
        
        // Add left and right rows
        for (int yy = y1 + 1; yy < y2; yy++) {
            Cell left = region.getCellOrNull(x1, yy);
            if (left != null && predicate.evalute(left)) {
                cells.add(left);
            }
            
            Cell right = region.getCellOrNull(x2, yy);
            if (right != null && predicate.evalute(right)) {
                cells.add(right);
            }
        }

        // Add the bottom row
        for (int xx = x1; xx <= x2; xx++) {
            Cell cell = region.getCellOrNull(xx, y2);
            if (cell != null && predicate.evalute(cell)) {
                cells.add(cell);
            }
        }

        return cells;
    }
    
    public List<Cell> getAdjacentCells() {
        ArrayList<Cell> adjacent = new ArrayList<Cell>(8);
        
        for (Direction d : Direction.values()) {
            int xx = x + d.dx;
            int yy = y + d.dy;
            
            if (region.containsPoint(xx, yy)) {
                adjacent.add(region.getCell(xx, yy));
            }
        }
        
        return adjacent;
    }

    public List<Cell> getAdjacentCellsOfType(CellType type) {
        ArrayList<Cell> adjacent = new ArrayList<Cell>(8);
        
        for (Direction d : Direction.values()) {
            int xx = x + d.dx;
            int yy = y + d.dy;
            
            if (region.containsPoint(xx, yy)) {
                Cell cell = region.getCell(xx, yy);
                if (cell.getType() == type) {
                    adjacent.add(cell);
                }
            }
        }
        
        return adjacent;
    }
    
    public int countPassableMainNeighbours() {
        int count = 0;
        
        for (Direction d : Direction.getMainDirections()) {
            if (getCellTowards(d).isPassable()) {
                count++;
            }
        }
        
        return count;
    }

    public Direction getDirection(Cell cell) {
        int dx = NumberUtils.signum(cell.x - x);
        int dy = NumberUtils.signum(cell.y - y);
        
        for (Direction d : Direction.values()) {
            if (dx == d.dx && dy == d.dy) {
                return d;
            }
        }
        
        return null;
    }
    
    public static Direction getDirectionOfPoint(int x1, int y1, int x2, int y2) {
        int dx = NumberUtils.signum(x2 - x1);
        int dy = NumberUtils.signum(y2 - y1);
        
        for (Direction d : Direction.values()) {
            if (dx == d.dx && dy == d.dy) {
                return d;
            }
        }
        
        return null;
    }
    
    public List<Cell> getAdjacentCellsInMainDirections() {
        ArrayList<Cell> adjacent = new ArrayList<Cell>(4);
        
        for (Direction d : Direction.getMainDirections()) {
            int xx = x + d.dx;
            int yy = y + d.dy;
            
            if (region.containsPoint(xx, yy)) {
                adjacent.add(region.getCell(xx, yy));
            }
        }
        
        return adjacent;
    }
    
    /**
     * Returns the cells between this cell and target. Does not include
     * the this cell and the target cell.
     */
    public List<Cell> getCellsBetween(Cell target) {
        List<Cell> cells = new ArrayList<Cell>(distance(target));
        
        // Bresenham's line algorithm is used below
        
        int x0 = x;
        int y0 = y;
        int x1 = target.x;
        int y1 = target.y;
        
        boolean steep = abs(y1 - y0) > abs(x1 - x0);
        if (steep) {
            int t0 = x0; x0 = y0; y0 = t0; // swap x0, y0
            int t1 = x1; x1 = y1; y1 = t1; // swap x1, y1
        }
        
        boolean reverse = x0 > x1;
        if (reverse) {
            int t0 = x0; x0 = x1; x1 = t0; // swap x0, x1
            int t1 = y0; y0 = y1; y1 = t1; // swap y0, y1
        }

        int deltax = x1 - x0;
        int deltay = abs(y1 - y0);
        int error = 0;
        int deltaerr = deltay;
        int y = y0;
        
        int ystep = (y0 < y1) ? 1 : -1;
        
        for (int x = x0; x < x1; x++) {
            if ((x != x0 || y != y0) && (x != x1 || y != y1)) {
                if (steep) {
                    cells.add(region.getCell(y, x));
                } else {
                    cells.add(region.getCell(x, y));
                }
            }
            error += deltaerr;
            if (2 * error >= deltax) {
                y += ystep;
                error -= deltax;
            }
        }

        if (reverse) {
            Collections.reverse(cells);
        }
        
        return cells;
    }
    
    public int getLighting() {
        return lighting;
    }
    
    public void resetLighting() {
        lighting = defaultLighting;
    }
    
    public void updateLighting() {
        int lightSourceEffectiveness = calculateLightSourceEffectiveness();
        if (lightSourceEffectiveness > 0) {
            int sight = lightSourceEffectiveness / 10;
            CellSet cells = new VisibilityChecker().getVisibleCells(this, sight);
            for (Cell cell : cells) {
                int distance = distance(cell);
                int level = lightSourceEffectiveness - 10 * distance;
                if (level > 0) {
                    cell.lighting += level;
                }
            }
        }
    }
    
    private int calculateLightSourceEffectiveness() {
        int effectiveness = 0;
        for (Item item : items) {
            effectiveness += item.getLighting();
        }
 
        if (creature != null) {
            effectiveness += creature.getLighting();
        }
        
        return lightPower + effectiveness;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y  + ")";
    }
}
    
