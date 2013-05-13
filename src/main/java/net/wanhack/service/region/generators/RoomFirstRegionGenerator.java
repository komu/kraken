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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.wanhack.model.common.Direction;
import net.wanhack.model.region.Cell;
import net.wanhack.model.region.CellSet;
import net.wanhack.model.region.CellType;
import net.wanhack.model.region.Door;
import net.wanhack.model.region.Region;
import net.wanhack.model.region.World;
import net.wanhack.utils.Probability;
import net.wanhack.utils.RandomUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * A simple region generator that first generates some rooms on the
 * region and then proceeds to make corridors between the rooms.
 * <p>
 * The algorithm starts by creating minRooms-maxRooms rooms of random
 * size on random places of the region. The algorithm tries to make the
 * rooms not overlap by trying to randomize the room <code>overlapTries</code>
 * times if it overlaps with existing rooms.
 * <p>
 * When the rooms have randomized and placed on map, the next step is to
 * make corridors between them. Two rooms are chosen at random, and a corridor
 * is made between them. If the corridor bumps into another room or the target,
 * then the corridor will end there. Then the next two rooms are chosen, and
 * a new corridor is created etc. This process will continue until all rooms
 * are reachable from all others.
 */
public class RoomFirstRegionGenerator implements RegionGenerator {

    private Region region;
    private int width;
    private int height;
    
    private int minRooms = 4;
    private int maxRooms = 10;
    private int roomMinWidth = 6;
    private int roomMaxWidth = 18;
    private int roomMinHeight = 5;
    private int roomMaxHeight = 9;
    private final Probability connectConnectedProbability = new Probability(50);
    private final Probability doorProbability = new Probability(30);
    private final Probability hiddenDoorProbability = new Probability(15);
    private final int overlapTries = 20;
    private final Random random = new Random();
    
    private final Log log = LogFactory.getLog(getClass());
    
    public Region generate(World world, String name, int level, String up, String down) {
        initRegionParameters(level);
        
        this.region = new Region(world, name, level, width, height);
        
        List<Room> rooms = createRooms();
        createCorridors(rooms);
        createDoors();
        addStairsUpAndDown(rooms, up, down);
        return region;
    }
    
    private void initRegionParameters(int level) {
        if (level < 5) {
            width = 80;
            height = 25;
            minRooms = 4;
            maxRooms = 10;
            roomMinWidth = 6;
            roomMaxWidth = 18;
            roomMinHeight = 5;
            roomMaxHeight = 9;
            
        } else if (level < 10) {
            width = 120;
            height = 30;
            minRooms = 6;
            maxRooms = 12;
            roomMinWidth = 6;
            roomMaxWidth = 18;
            roomMinHeight = 5;
            roomMaxHeight = 9;
            
        } else if (level < 20) {
            width = 160;
            height = 40;
            minRooms = 8;
            maxRooms = 16;
            roomMinWidth = 6;
            roomMaxWidth = 18;
            roomMinHeight = 5;
            roomMaxHeight = 9;

        } else {
            width = 200;
            height = 50;
            minRooms = 10;
            maxRooms = 25;
            roomMinWidth = 6;
            roomMaxWidth = 18;
            roomMinHeight = 5;
            roomMaxHeight = 9;
        }
    }

    private List<Room> createRooms() {
        int roomCount = minRooms + random.nextInt(maxRooms - minRooms + 1);
        
        List<Room> rooms = new ArrayList<Room>(roomCount);
        for (int i = 0; i < roomCount; i++) {
            rooms.add(createRoom());
        }
        
        return rooms;
    }

    private Room createRoom() {
        Room room = randomRoom();
        int tries = overlapTries;
        
        while (overlapsExisting(room) && tries-- > 0) {
            room = randomRoom();
        }
        
        room.addToRegion();
        return room;
    }
    
    private void createDoors() {
        for (Cell cell : region) {
            if (isDoorCandidate(cell) && doorProbability.check()) {
                boolean hidden = hiddenDoorProbability.check();
                cell.setState(new Door(hidden));
            }
        }
    }

    private boolean isDoorCandidate(Cell cell) {
        if (cell.getType() != CellType.HALLWAY_FLOOR) {
            return false;
        }
        
        Cell roomNeighbour = null;
        Cell hallwayNeighbour = null;
        int walls = 0;
        for (Cell neighbour : cell.getAdjacentCellsInMainDirections()) {
            if (neighbour.getType() == CellType.ROOM_FLOOR) {
                roomNeighbour = neighbour;
            } else if (neighbour.getType() == CellType.HALLWAY_FLOOR) {
                hallwayNeighbour = neighbour;
            } else if (neighbour.getType() == CellType.ROOM_WALL) {
                walls++;
            }
        }
        
        if (roomNeighbour != null && hallwayNeighbour != null && walls == 2) {
            
            Direction room = cell.getDirection(roomNeighbour);
            Direction hall = cell.getDirection(hallwayNeighbour);
            if (room.isOpposite(hall)) {
                return true;
            }
        }
           
        return false;
    }

    private void createCorridors(List<Room> rooms) {
        for (int count = 0; !allConnected(rooms); count++) {
            Room room1 = RandomUtils.randomItem(rooms);
            Room room2 = random(rooms, room1);
            
            if (!connected(room1, room2) || connectConnected()) {
                connect(room1, room2);
            }
            
            if (count > 1000) {
                log.warn("Count exceeded, bailing out.");
                break;
            }
        }
    }

    private boolean connectConnected() {
        return connectConnectedProbability.check();
    }

    private void connect(Room start, Room goal) {
        Cell startCell = start.getRandomCell(random);
        Cell goalCell = goal.getRandomCell(random);
        
        Cell previous = null;
        for (Cell cell : createPath(startCell, goalCell)) {
            if (cell.getType() != CellType.ROOM_FLOOR) {
                cell.setType(CellType.HALLWAY_FLOOR);
            }
            
            if (!start.contains(cell)) {
                for (Cell adjacent : cell.getAdjacentCellsInMainDirections()) {
                    if (adjacent != previous && adjacent.isPassable()) {
                        return;
                    }
                }
            }
            
            previous = cell;
        }
    }

    private List<Cell> createPath(Cell start, Cell goal) {
        return new CorridorPathSearcher(region).findShortestPath(start, goal);
    }

    private <T> T random(List<T> items, T invalid) {
        T result = invalid;
        while (result == invalid) {
            result = items.get(random.nextInt(items.size()));
        }
        return result;
    }
    
    private boolean allConnected(List<Room> rooms) {
        Room start = rooms.get(0);
        for (Room room : rooms) {
            if (!connected(start, room)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean connected(Room room1, Room room2) {
        return room1.getMiddleCell().isReachable(room2.getMiddleCell());
    }

    private boolean overlapsExisting(Room room) {
        for (int yy = 0; yy < room.h; yy++) {
            for (int xx = 0; xx < room.w; xx++) {
                Cell cell = region.getCell(room.x + xx, room.y + yy);
                if (cell.getType() != CellType.WALL) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addStairsUpAndDown(List<Room> rooms, String upRegion, String downRegion) {
        Room stairsUpRoom = RandomUtils.randomItem(rooms);
        // TODO: copy paste from MazeRegionGenerator
        
        CellSet empty = region.getRoomFloorCells();
        if (empty.size() < 2) {
            throw new IllegalStateException("not enough empty cells to place stairs");
        }
        
        Cell stairsUp = stairsUpRoom.getRandomCell(random);
        
        stairsUp.setType(CellType.STAIRS_UP);
        if (upRegion != null) {
            region.addPortal(stairsUp.x, stairsUp.y, upRegion, "from down", true);
        }
        region.addStartPoint("from up", stairsUp.x, stairsUp.y);

        while (true) {
            Room stairsDownRoom = random(rooms, stairsUpRoom);
            Cell stairsDown = stairsDownRoom.getRandomCell(random);
            if (stairsDown != stairsUp && region.findPath(stairsUp, stairsDown) != null) {
                stairsDown.setType(CellType.STAIRS_DOWN);
                region.addPortal(stairsDown.x, stairsDown.y, downRegion, "from up", false);
                region.addStartPoint("from down", stairsDown.x, stairsDown.y);
                return;
            }
        }
    }

    private Room randomRoom() {
        // TODO: copy-paste from MazeRegionGenerator
        int w = roomMinWidth + random.nextInt(1 + roomMaxWidth - roomMinWidth);
        int h = roomMinHeight + random.nextInt(1 + roomMaxHeight - roomMinHeight);
        int x = 1 + random.nextInt(width - w - 2);
        int y = 1 + random.nextInt(height - h - 2);
        
        return new Room(region, x, y, w, h);
    }
    
    private static class Room {
        private final Region region;
        private final int x;
        private final int y;
        private final int w;
        private final int h;
        
        public Room(Region region, int x, int y, int w, int h) {
            this.region = region;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
        
        public boolean contains(Cell cell) {
            return cell.x >= x 
                && cell.x < x + w
                && cell.y >= y 
                && cell.y < y + h;
        }
        
        public Cell getRandomCell(Random random) {
            int xx = x + 1 + random.nextInt(w - 2);
            int yy = y + 1 + random.nextInt(h - 2);
            
            return region.getCell(xx, yy);
        }

        public Cell getMiddleCell() {
            int xx = x + w / 2;
            int yy = y + h / 2;
            
            return region.getCell(xx, yy);
        }

        private void addToRegion() {
            for (int yy = 1; yy < h - 1; yy++) {
                for (int xx = 1; xx < w - 1; xx++) {
                    region.getCell(x + xx, y + yy).setType(CellType.ROOM_FLOOR);
                }
            }
            
            for (int xx = 0; xx < w; xx++) {
                region.getCell(x + xx, y).setType(CellType.ROOM_WALL);
                region.getCell(x + xx, y + h - 1).setType(CellType.ROOM_WALL);
            }
            
            for (int yy = 0; yy < h; yy++) {
                region.getCell(x, y + yy).setType(CellType.ROOM_WALL);
                region.getCell(x + w - 1, y + yy).setType(CellType.ROOM_WALL);
            }
        }
    }
}
