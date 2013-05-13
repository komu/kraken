/*
 *  Copyright 2005-2006 The Wanhack Team
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import net.wanhack.model.GameRef;
import net.wanhack.model.IGame;
import net.wanhack.model.creature.Player;
import net.wanhack.model.item.Item;
import net.wanhack.ui.game.action.DropItemAction;

public class InventoryView extends JPanel {
    
    private final JList list = new JList();
    private GameRef gameRef;

    public InventoryView() {
        super(new BorderLayout());
    
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new InventoryCellRenderer());
        list.setFocusable(false);
        list.addMouseListener(new ListMouseListener());
        list.setBackground(Color.BLACK);
        
        setPreferredSize(new Dimension(200, 200));
        
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void setGameRef(GameRef gameRef) {
        this.gameRef = gameRef;
    }

    public void update(IGame game) {
        if (game != null) {
            Player player = game.getPlayer();
            
            Set<Item> inventory = player.getInventoryItems();
            List<ItemInfo> infos = new ArrayList<ItemInfo>(inventory.size() + 10);
            for (Item item : inventory) {
                infos.add(new ItemInfo(item, false));
            }

            for (Item item : player.getActivatedItems()) {
                infos.add(new ItemInfo(item, true));
            }

            Collections.sort(infos);
            
            list.setListData(infos.toArray());
        }
    }
    
    private class ListMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                showPopupMenu(e);
            }
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                showPopupMenu(e);
            }
        }

        private void showPopupMenu(MouseEvent e) {
            if (list.isSelectionEmpty()) {
                int index = list.locationToIndex(e.getPoint());
                if (index != -1) {
                    list.setSelectedIndex(index);
                }
            }

            final ItemInfo item = (ItemInfo) list.getSelectedValue();
            if (item != null) {
                final JPopupMenu popup = new JPopupMenu();
                
                if (!item.isInUse()) {
                    popup.add(new DropItemAction(gameRef, item.getItem()));
                }
                
                popup.show(list, e.getX(), e.getY());
            }
        }
    }
}
