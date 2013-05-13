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
package net.wanhack.ui.console;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import net.wanhack.model.common.Direction;


public class SelectDirectionDialog extends JDialog {
    
    private Direction selectedDirection;

    public SelectDirectionDialog(Frame owner) {
        super(owner, "Select Direction");

        setModal(true);
        
        getContentPane().setLayout(new GridLayout(3, 3));
        
        add(new JButton(new SelectDirectionAction(Direction.NW)));
        add(new JButton(new SelectDirectionAction(Direction.NORTH)));
        add(new JButton(new SelectDirectionAction(Direction.NE)));
        
        add(new JButton(new SelectDirectionAction(Direction.WEST)));
        add(new JPanel());
        add(new JButton(new SelectDirectionAction(Direction.EAST)));

        add(new JButton(new SelectDirectionAction(Direction.SW)));
        add(new JButton(new SelectDirectionAction(Direction.SOUTH)));
        add(new JButton(new SelectDirectionAction(Direction.SE)));
        
        addInput("UP",        Direction.NORTH);
        addInput("DOWN",      Direction.SOUTH);
        addInput("LEFT",      Direction.WEST);
        addInput("RIGHT",     Direction.EAST);
        
        // Movement with numlock on
        addInput("NUMPAD1",   Direction.SW);
        addInput("NUMPAD2",   Direction.SOUTH);
        addInput("NUMPAD3",   Direction.SE);
        addInput("NUMPAD4",   Direction.WEST);
        addInput("NUMPAD6",   Direction.EAST);
        addInput("NUMPAD7",   Direction.NW);
        addInput("NUMPAD8",   Direction.NORTH);
        addInput("NUMPAD9",   Direction.NE);
        
        addInput("ESCAPE",    "escape");
        
        ActionMap actionMap = getRootPane().getActionMap();
        for (Direction dir : Direction.values()) {
            actionMap.put(dir, new SelectDirectionAction(dir));
        }
        
        actionMap.put("escape", new SelectDirectionAction(null));
        
        pack();
        setLocationRelativeTo(owner);        
    }
    
    public static Direction selectDirection(Frame frame) {
        SelectDirectionDialog dlg = new SelectDirectionDialog(frame);
        dlg.setVisible(true);
        return dlg.getSelectedDirection();
    }
    
    private Direction getSelectedDirection() {
        return selectedDirection;
    }

    private void addInput(String keyStroke, Object actionKey) {
        InputMap inputMap = getRootPane().getInputMap();
        inputMap.put(KeyStroke.getKeyStroke(keyStroke), actionKey);
    }
    
    private class SelectDirectionAction extends AbstractAction {
        private final Direction dir;

        public SelectDirectionAction(Direction dir) {
            super((dir == null) ? "" : dir.getShortName());
            this.dir = dir;
        }

        public void actionPerformed(ActionEvent e) {
            selectedDirection = dir;
            setVisible(false);
        }
    }
}
