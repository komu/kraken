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
package net.wanhack.ui.game.tile;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;

import net.wanhack.model.creature.Creature;
import net.wanhack.model.item.Item;
import net.wanhack.model.region.Cell;


public class DefaultTileProvider implements TileProvider {

    private static final int TILE_WIDTH = 8;
    private static final int TILE_HEIGHT = 13;
    private final Font font = new Font("Monospaced", Font.PLAIN, 14);
    private static final char[] chars = new char[1];
    private static final Color ROOM_FLOOR_INVISIBLE = new Color(80, 80, 80);
    private static final Color WALL_VISIBLE = Color.DARK_GRAY;
    private static final Color WALL_INVISIBLE = WALL_VISIBLE.darker();
    private static final Color DOOR_VISIBLE = new Color(100, 100, 0);
    private static final Color DOOR_INVISIBLE = DOOR_VISIBLE.darker();
    
    public Dimension getDimensions(int width, int height) {
        return new Dimension(width * TILE_WIDTH, height * TILE_HEIGHT);
    }

    public int getTileWidth() {
        return TILE_WIDTH;
    }
    
    public int getTileHeight() {
        return TILE_HEIGHT;
    }
    
    public void drawCell(Graphics2D g, Cell cell, boolean visible) {
        int x = cell.x;
        int y = cell.y;
        
        switch (cell.getType()) {
        case HALLWAY_FLOOR:
        case ROOM_FLOOR:
            drawFloor(g, cell, visible, false);
            break;
        case UNDIGGABLE_WALL:
        case ROOM_WALL:
        case WALL:
            drawWall(g, x, y, visible); 
            break;
        case STAIRS_UP:
            drawStairs(g, cell, true, visible);
            break;
        case STAIRS_DOWN:
            drawStairs(g, cell, false, visible);
            break;
        case OPEN_DOOR:
            drawDoor(g, cell, true, visible);
            break;
        case CLOSED_DOOR:
            drawDoor(g, cell, false, visible);
            break;
        }
    }

    public void drawCreature(Graphics2D g, Cell cell, Creature creature) {
        drawFloor(g, cell, true, true);
        drawLetter(g, cell.x, cell.y, creature.getLetter(), creature.getColor());
    }
    
    public void drawSelection(Graphics2D g, Cell cell) {
        int xx = cell.x * TILE_WIDTH;
        int yy = cell.y * TILE_HEIGHT;
        
        g.setPaint(new Color(0.8f, 0.3f, 0.3f, 0.5f));
        g.fillRect(xx, yy, TILE_WIDTH, TILE_HEIGHT);            
    }
    
    private void drawLetter(Graphics2D g, int x, int y, char letter, Paint paint) {
        int xx = x * TILE_WIDTH;
        int yy = y * TILE_HEIGHT;
        g.setFont(font);
        g.setPaint(paint);
        
        chars[0] = letter;
        g.drawChars(chars, 0, 1, xx, yy + 10);
    }

    public void drawItem(Graphics2D g, Cell cell, Item item) {
        drawFloor(g, cell, true, true);
        
        drawLetter(g, cell.x, cell.y, item.getLetter(), item.getColor());
    }
    
    private void drawStairs(Graphics2D g, Cell cell, boolean up, boolean visible) {
        drawFloor(g, cell, visible, false);
        drawLetter(g, cell.x, cell.y, up ? '<' : '>', Color.BLACK);
    }

    private void drawDoor(Graphics2D g, Cell cell, boolean open, boolean visible) {
        drawFloor(g, cell, visible, false);
        drawLetter(g, cell.x, cell.y, open ? '\'' : '+', visible ? DOOR_VISIBLE : DOOR_INVISIBLE);
    }
    
    private void drawFloor(Graphics2D g, Cell cell, boolean visible, boolean shadow) {
        int xx = cell.x * TILE_WIDTH;
        int yy = cell.y * TILE_HEIGHT;
        
        if (visible) {
            g.setPaint(getFloorColor(cell.getLighting(), shadow));
        } else {
            g.setPaint(ROOM_FLOOR_INVISIBLE);
        }
        
        g.fillRect(xx, yy, TILE_WIDTH, TILE_HEIGHT);            
    }
    
    private Paint getFloorColor(int lighting, boolean shadow) {
        int d = Math.max(0, Math.min(50 + lighting, 255));
        if (shadow && d > 180) {
            d = Math.max(0, d - 35);
        }
        
        return new Color(d, d, d);
    }

    private void drawWall(Graphics2D g, int x, int y, boolean visible) {
        int xx = x * TILE_WIDTH;
        int yy = y * TILE_HEIGHT;
        
        g.setPaint(visible ? WALL_VISIBLE : WALL_INVISIBLE);
        g.fillRect(xx, yy, TILE_WIDTH, TILE_HEIGHT);            
    }
}
