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
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.KeyStroke;

import net.wanhack.model.GameRef;
import net.wanhack.model.IGame;


public class GameActionSet {
    
    private final List<GameAction> actions = new ArrayList<GameAction>();

    public GameActionSet() {
        actions.add(new WieldAction());
        actions.add(new WearAction());
        actions.add(new RestAction());
        actions.add(new TalkAction());
        actions.add(new OpenDoorAction());
        actions.add(new CloseDoorAction());
        actions.add(new PickupAction());
        actions.add(new DropAction());
        actions.add(new EatAction());
        actions.add(new FlingAction());
        actions.add(new SearchAction());
    }
    
    public void setGameRef(GameRef gameRef) {
        for (GameAction action : actions) {
            action.setGameRef(gameRef);
        }
    }

    public List<GameAction> getActions() {
        return actions;
    }

    private static class WieldAction extends GameAction {
        public WieldAction() {
            super("Wield Weapon");
            putValue(MNEMONIC_KEY, KeyEvent.VK_W);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("W"));
        }
        
        @Override
        protected void actionPerformed(ActionEvent e, IGame game) {
            game.wield();
        }
    }

    private static class WearAction extends GameAction {
        public WearAction() {
            super("Wear Armor");
            putValue(MNEMONIC_KEY, KeyEvent.VK_A);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("shift W"));
        }
        
        @Override
        protected void actionPerformed(ActionEvent e, IGame game) {
            game.wear();
        }
    }
    
    private static class RestAction extends GameAction {
        public RestAction() {
            super("Rest");
            putValue(MNEMONIC_KEY, KeyEvent.VK_R);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("R"));
        }
        
        @Override
        protected void actionPerformed(ActionEvent e, IGame game) {
            game.rest(-1);
        }
    }
    
    private static class TalkAction extends GameAction {
        public TalkAction() {
            super("Talk");
            putValue(MNEMONIC_KEY, KeyEvent.VK_T);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("T"));
        }
        
        @Override
        protected void actionPerformed(ActionEvent e, IGame game) {
            game.talk();
        }
    }

    private static class OpenDoorAction extends GameAction {
        public OpenDoorAction() {
            super("Open Door");
            putValue(MNEMONIC_KEY, KeyEvent.VK_O);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("O"));
        }

        @Override
        protected void actionPerformed(ActionEvent e, IGame game) {
            game.openDoor();
        }
    }
    
    private static class CloseDoorAction extends GameAction {
        public CloseDoorAction() {
            super("Close Door");
            putValue(MNEMONIC_KEY, KeyEvent.VK_C);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("C"));
        }
        
        @Override
        protected void actionPerformed(ActionEvent e, IGame game) {
            game.closeDoor();
        }
    }

    private static class PickupAction extends GameAction {
        public PickupAction() {
            super("Pick up");
            putValue(MNEMONIC_KEY, KeyEvent.VK_P);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("COMMA"));
        }
        
        @Override
        protected void actionPerformed(ActionEvent e, IGame game) {
            game.pickup();
        }
    }
    
    private static class DropAction extends GameAction {
        public DropAction() {
            super("Drop");
            putValue(MNEMONIC_KEY, KeyEvent.VK_D);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("D"));
        }
        
        @Override
        protected void actionPerformed(ActionEvent e, IGame game) {
            game.drop();
        }
    }

    private static class EatAction extends GameAction {
        public EatAction() {
            super("Eat");
            putValue(MNEMONIC_KEY, KeyEvent.VK_E);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("E"));
        }
        
        @Override
        protected void actionPerformed(ActionEvent e, IGame game) {
            game.eat();
        }
    }
    
    private static class FlingAction extends GameAction {
        public FlingAction() {
            super("Fling");
            putValue(MNEMONIC_KEY, KeyEvent.VK_F);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("F"));
        }
        
        @Override
        protected void actionPerformed(ActionEvent e, IGame game) {
            game.fling();
        }
    }
    
    private static class SearchAction extends GameAction {
        public SearchAction() {
            super("Search");
            putValue(MNEMONIC_KEY, KeyEvent.VK_S);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("S"));
        }
        
        @Override
        protected void actionPerformed(ActionEvent e, IGame game) {
            game.search();
        }
    }
}
