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
package net.wanhack.service.region.generators;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.wanhack.model.common.Direction;
import net.wanhack.model.region.Cell;
import net.wanhack.model.region.CellSet;
import net.wanhack.model.region.CellType;
import net.wanhack.model.region.Region;
import net.wanhack.model.region.World;
import net.wanhack.utils.Probability;


/**
 * Generates randomized regions. Follows the algorithm specified in
 * http://www.aarg.net/~minam/dungeon_design.html
 */
public class MazeRegionGenerator implements RegionGenerator {

    private Region region;
    private int width;
    private int height;
    
    private final Probability randomness = new Probability(40);
    private final int sparseness = 5;
    private final Probability deadEndsRemoved = new Probability(90);
    
    private final int minRooms = 2;
    private final int maxRooms = 8;
    private final int roomMinWidth = 3;
    private final int roomMaxWidth = 10;
    private final int roomMinHeight = 3;
    private final int roomMaxHeight = 7;
    
    private final Random random = new Random();
    private final List<Direction> directions = 
        new ArrayList<Direction>(Arrays.asList(Direction.getMainDirections()));
    
    public Region generate(World world, String name, int level, String up, String down) {
        this.region = new Region(world, name, level, 80, 25);
        this.width = region.getWidth();
        this.height = region.getHeight();
        
        generateMaze();
        sparsify();
        addLoops();
        addRooms();
        addStairsUpAndDown(up, down);
        return region;
    }
    
    private void addStairsUpAndDown(String upRegion, String downRegion) {
        CellSet empty = region.getRoomFloorCells();
        if (empty.size() < 2) {
            throw new IllegalStateException("not enough empty cells to place stairs");
        }
        
        Cell stairsUp = empty.get(random.nextInt(empty.size()));
        
        stairsUp.setType(CellType.STAIRS_UP);
        if (upRegion != null) {
            region.addPortal(stairsUp.x, stairsUp.y, upRegion, "from down", true);
        }
        region.addStartPoint("from up", stairsUp.x, stairsUp.y);

        while (true) {
            Cell stairsDown = empty.get(random.nextInt(empty.size()));
            if (stairsDown != stairsUp && region.findPath(stairsUp, stairsDown) != null) {
                stairsDown.setType(CellType.STAIRS_DOWN);
                region.addPortal(stairsDown.x, stairsDown.y, downRegion, "from up", false);
                region.addStartPoint("from down", stairsDown.x, stairsDown.y);
                return;
            }
        }
    }
    
    /**
     * Generate random maze without loops.
     */
    private void generateMaze() {
        int randomX = 1 + random.nextInt(width - 2);
        int randomY = 1 + random.nextInt(height - 2);
        
        Cell current = region.getCell(randomX, randomY);
        current.setType(CellType.HALLWAY_FLOOR);
        
        CellSet candidates = new CellSet(region);
        while (current != null) {
            current = generatePathFrom(current, candidates, null, 3, false);
            
            if (current == null) {
                current = randomCandidate(candidates);
            }
        }
    }
    
    private Cell generatePathFrom(Cell current, 
                                  CellSet candidates,
                                  CellSet visited,
                                  int gridsize,
                                  boolean stopOnEmpty) {

        int currentX = current.x;
        int currentY = current.y;
        
        for (Direction dir : getDirections()) {
            int xx = currentX + gridsize * dir.dx;
            int yy = currentY + gridsize * dir.dy;
            
            if (isOk(xx, yy) && (visited == null || !visited.contains(xx, yy))) {
                Cell cell = region.getCell(xx, yy);
                
                if (!cell.isPassable() && cell.getType() != CellType.UNDIGGABLE_WALL) {
                    // put floor on  the cells between current and target 
                    for (int i = 1; i < gridsize; i++) {
                        int xxx = currentX + i * dir.dx;
                        int yyy = currentY + i * dir.dy;
                        region.getCell(xxx, yyy).setType(CellType.HALLWAY_FLOOR);
                    }

                    cell.setType(CellType.HALLWAY_FLOOR);
                    
                    if (candidates != null) {
                        candidates.add(cell);
                    }
                    
                    return cell;
                } else if (stopOnEmpty) {
                    // put floor on  the cells between current and target 
                    for (int i = 1; i < gridsize; i++) {
                        int xxx = currentX + i * dir.dx;
                        int yyy = currentY + i * dir.dy;
                        region.getCell(xxx, yyy).setType(CellType.HALLWAY_FLOOR);
                    }
                    
                    return null;
                }
            }
        }
        
        if (candidates != null) {
            candidates.remove(current);
        }
        return null;
    }
    
    // get the directions, possibly randomizing them
    private List<Direction> getDirections() {
        if (randomness.check()) {
            Collections.shuffle(directions, random);
        }
        
        return directions;
    }
    
    /**
     * Apply sparseness.
     */
    private void sparsify() {
        for (int i = 0; i < sparseness; i++) {
            shortenDeadEnds();
        }
    }

    private void shortenDeadEnds() {
        CellSet removed = new CellSet(region);
        
        for (Cell cell : region) {
            if (isDeadEnd(cell)) {
                removed.add(cell);
            }
        }
        
        for (Cell cell : removed) {
            cell.setType(CellType.WALL);
        }
    }
    
    private void addLoops() {
        for (Cell cell : region) {
            if (isDeadEnd(cell) && deadEndsRemoved.check()) {
                removeDeadEnd(cell);
            }
        }
    }
    
    private void removeDeadEnd(Cell start) {
        CellSet visited = new CellSet(region);
        
        for (Cell current = start; current != null; ) {
            visited.add(current);
            current = generatePathFrom(current, null, visited, 3, true);
        }
    }
    
    private void addRooms() {
        int rooms = minRooms + random.nextInt(1 + maxRooms - minRooms);
        
        for (int i = 0; i < rooms; i++) {
            addRoom();
        }
    }
    
    private void addRoom() {
        Dimension room = randomRoomDimensions();
        
        int x = 2 + random.nextInt(width - room.width - 4);
        int y = 2 + random.nextInt(height - room.height - 4);
        
        createRoom(x, y, room);
    }
    
    private void createRoom(int x, int y, Dimension dims) {
        for (int yy = 0; yy <= dims.height; yy++) {
            for (int xx = 0; xx <= dims.width; xx++) {
                region.getCell(x + xx, y + yy).setType(CellType.ROOM_FLOOR);
            }
        }
    }

    private Dimension randomRoomDimensions() {
        int w = roomMinWidth + random.nextInt(1 + roomMaxWidth - roomMinWidth);
        int h = roomMinHeight + random.nextInt(1 + roomMaxHeight - roomMinHeight);
        
        return new Dimension(w, h);
    }

    private static boolean isDeadEnd(Cell cell) {
        return cell.isPassable() && cell.countPassableMainNeighbours() == 1;
    }

    private Cell randomCandidate(CellSet candidates) {
        if (candidates.isEmpty()) {
            return null;
        } else {
            int index = random.nextInt(candidates.size());
            return candidates.get(index);
        }
    }
    
    private boolean isOk(int x, int y) {
        return x > 1 && x < width - 1
            && y > 1 && y < height - 1;
    }
}
