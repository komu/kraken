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
package net.wanhack.ui.game.action;

import java.awt.event.ActionEvent;

import net.wanhack.model.GameRef;
import net.wanhack.model.IGame;
import net.wanhack.model.item.Item;


public class DropItemAction extends GameAction {

    private final Item item;

    public DropItemAction(GameRef game, Item item) {
        super("Drop");
        setEnabled(true);
        setGameRef(game);
        
        this.item = item;
    }
    
    @Override
    protected void actionPerformed(ActionEvent e, IGame game) {
        game.drop(item);
    }
}
