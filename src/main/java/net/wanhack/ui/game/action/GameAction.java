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

import javax.swing.AbstractAction;

import net.wanhack.model.ActionCallback;
import net.wanhack.model.GameRef;
import net.wanhack.model.IGame;


public abstract class GameAction extends AbstractAction {

    private GameRef gameRef;

    public GameAction(String name) {
        super(name);
        
        setEnabled(false);
    }

    public void setGameRef(GameRef gameRef) {
        this.gameRef = gameRef;
        
        setEnabled(gameRef != null);
    }
    
    public final void actionPerformed(final ActionEvent e) {
        if (gameRef != null) {
            gameRef.scheduleAction(new ActionCallback() {
                public void execute(IGame game) {
                    actionPerformed(e, game);
                }
            });
        }
    }

    protected abstract void actionPerformed(ActionEvent e, IGame game);
}
