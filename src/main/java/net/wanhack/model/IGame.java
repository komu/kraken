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
package net.wanhack.model;

import java.io.IOException;
import java.io.ObjectOutputStream;

import net.wanhack.common.WriteOperation;
import net.wanhack.model.common.Direction;
import net.wanhack.model.creature.Player;
import net.wanhack.model.item.Item;
import net.wanhack.model.region.Cell;
import net.wanhack.model.region.Region;


public interface IGame {

    @WriteOperation
    void start();
    
    @WriteOperation
    void talk();
    
    @WriteOperation
    void openDoor();
    
    @WriteOperation
    void closeDoor();
    
    @WriteOperation
    void pickup();
    
    @WriteOperation
    void wield();
    
    @WriteOperation
    void wear();
    
    @WriteOperation
    void drop();
    
    @WriteOperation
    void drop(Item item);
    
    @WriteOperation
    void eat();
    
    @WriteOperation
    void fling();
    
    @WriteOperation
    void search();

    @WriteOperation
    void movePlayer(Direction direction);
    
    @WriteOperation
    void runTowards(Direction direction);
    
    @WriteOperation
    void movePlayerVertically(boolean up);
    
    @WriteOperation
    void skipTurn();
    
    @WriteOperation
    void rest(int maxTurns);

    @WriteOperation
    void revealCurrentRegion();

    Region getCurrentRegion();
    Player getPlayer();
    int getScore();
    
    int getTime();
    int getDungeonLevel();
    int getMaxDungeonLevel();
    Cell getCellInFocus();
    Cell getSelectedCell();
    void save(ObjectOutputStream oos) throws IOException;
}
