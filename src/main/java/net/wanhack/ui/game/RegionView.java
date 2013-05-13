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
package net.wanhack.ui.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;

import net.wanhack.model.GameRef;
import net.wanhack.model.IGame;
import net.wanhack.model.SimpleQueryCallback;
import net.wanhack.model.creature.Creature;
import net.wanhack.model.creature.Player;
import net.wanhack.model.item.Item;
import net.wanhack.model.region.Cell;
import net.wanhack.model.region.Region;
import net.wanhack.ui.game.tile.DefaultTileProvider;
import net.wanhack.ui.game.tile.TileProvider;


public class RegionView extends JComponent {

    private GameRef gameRef;
    private final TileProvider tileProvider = new DefaultTileProvider();
    private boolean translate = true;

    public RegionView() {
        setBackground(Color.BLACK);
    }
    
    public void setGameRef(GameRef gameRef) {
        this.gameRef = gameRef;
        repaint();
    }
    
    public void setTranslate(boolean translate) {
        this.translate = translate;
    }
    
    public boolean isTranslate() {
        return translate;
    }

    @Override
    public void paint(Graphics g) {
        final Graphics2D g2 = (Graphics2D) g;
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_OFF);
        
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        
        paintBackground(g2);
        
        if (gameRef != null) {
            gameRef.executeQuery(new SimpleQueryCallback() {
                public void execute(IGame game) {
                    if (game.getCurrentRegion() != null) {
                        transformFocusToCell(g2, game.getCellInFocus());
                        
                        Player player = game.getPlayer();
                        for (Cell cell : game.getCurrentRegion()) {
                            paintCell(g2, cell, player);
                        }
                        
                        if (game.getSelectedCell() != null) {
                            tileProvider.drawSelection(g2, game.getSelectedCell());
                        }
                    }
                }
            });
        }
    }

    private void transformFocusToCell(Graphics2D g2, Cell cell) {
        AffineTransform transform = getTransform(cell);
        if (transform != null) {
            g2.transform(transform);
        }
    }
    
    private AffineTransform getTransform(Cell cell) {
        int width = getWidth();
        int height = getHeight();
        int regionWidth = cell.getRegion().getWidth();
        int regionHeight = cell.getRegion().getHeight();
        int tileWidth = tileProvider.getTileWidth();
        int tileHeight = tileProvider.getTileHeight();
        int requiredWidth = tileWidth * regionWidth;
        int requiredHeight = tileHeight * regionHeight; 

        if (width >= requiredWidth && height >= requiredHeight) {
            return null;
        }

        if (translate) {
            // translate
            int x = tileWidth * cell.x;
            int y = tileHeight * cell.y;
    
            int dx = Math.max(0, Math.min(x - width / 2, requiredWidth - width));
            int dy = Math.max(0, Math.min(y - height / 2, requiredHeight - height));
            
            return AffineTransform.getTranslateInstance(-dx, -dy);
        } else {
            // scale
            float widthRatio = ((float) width) / requiredWidth;
            float heightRatio = ((float) height) / requiredHeight;
            float scale = Math.min(widthRatio, heightRatio);
            
            return AffineTransform.getScaleInstance(scale, scale);
        }
    }

    private void paintBackground(Graphics2D g2) {
        g2.setPaint(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
    
    private void paintCell(Graphics2D g2, Cell cell, Player player) {
        if (player.canSee(cell)) {
            Creature creature = cell.getCreature();
            if (creature != null) {
                tileProvider.drawCreature(g2, cell, creature);
            } else if (!cell.getItems().isEmpty()) {
                Item item = cell.getLargestItem();
                tileProvider.drawItem(g2, cell, item);
            } else {
                tileProvider.drawCell(g2, cell, true);                
            }
            
        } else if (cell.getHasBeenSeen()) {
            tileProvider.drawCell(g2, cell, false);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return tileProvider.getDimensions(Region.DEFAULT_REGION_WIDTH,
                                          Region.DEFAULT_REGION_HEIGHT);
    }
}
