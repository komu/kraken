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

import java.awt.Dimension;
import java.awt.Graphics2D;

import net.wanhack.model.creature.Creature;
import net.wanhack.model.item.Item;
import net.wanhack.model.region.Cell;

public interface TileProvider {
    void drawCell(Graphics2D g, Cell cell, boolean visible);
    void drawCreature(Graphics2D g, Cell cell, Creature creature);
    void drawItem(Graphics2D g, Cell cell, Item item);
    void drawSelection(Graphics2D g, Cell cell);
    Dimension getDimensions(int width, int height);
    int getTileWidth();
    int getTileHeight();
}
