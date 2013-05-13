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

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import net.wanhack.model.item.Item;


import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SelectItemsDialog extends JDialog {

    private final JList itemList;
    private Set<Item> selectedItems = Collections.emptySet();
    
    public SelectItemsDialog(Frame owner, String message, Collection<? extends Item> items) {
        super(owner, message);
        setModal(true);
        
        itemList = new JList(items.toArray());
        itemList.setCellRenderer(new ItemCellRenderer());

        initContent();
        
        itemList.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
        itemList.getActionMap().put("cancel", new CancelAction());
        
        pack();
        setLocationRelativeTo(owner);        
    }
    
    private Set<Item> getSelectedItems() {
        return selectedItems;
    }

    private void initContent() {
        CellConstraints cc = new CellConstraints();
        
        FormLayout layout = new FormLayout("pref", "pref, 8dlu, pref");

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setDefaultDialogBorder();
        
        builder.add(new JScrollPane(itemList));
        builder.add(createButtonBar(),  cc.xy(1, 3));
        
        setContentPane(builder.getPanel());
    }
    
    private void setAllowMultipleSelections(boolean b) {
        if (b) {
            itemList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        } else {
            itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
    }

    private JPanel createButtonBar() {
        JButton ok = new JButton(new OkAction());
        JButton cancel = new JButton(new CancelAction());
        
        getRootPane().setDefaultButton(ok);
        
        return ButtonBarFactory.buildOKCancelBar(ok, cancel);
    }

    public static Set<Item> selectItems(Frame frame, 
                                        String message,
                                        Collection<? extends Item> items) {
        SelectItemsDialog dlg = new SelectItemsDialog(frame, message, items);
        dlg.setAllowMultipleSelections(true);
        dlg.setVisible(true);
        return dlg.getSelectedItems();
    }
    
    public static Item selectItem(Frame frame, 
                                  String message,
                                  Collection<? extends Item> items) {
        SelectItemsDialog dlg = new SelectItemsDialog(frame, message, items);
        dlg.setAllowMultipleSelections(false);
        dlg.setVisible(true);
        Set<Item> selected = dlg.getSelectedItems();
        if (!selected.isEmpty()) {
            return selected.iterator().next();
        } else {
            return null;
        }
    }
    
    private static class ItemCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            Item item = (Item) value;
            if (item != null) {
                setText(item.getTitle());
            } else {
                setText("");
            }
            return this;
        }
    }

    private class OkAction extends AbstractAction {
        public OkAction() {
            super("Ok");
        }
        
        public void actionPerformed(ActionEvent e) {
            selectedItems = new LinkedHashSet<Item>();
            
            for (Object obj : itemList.getSelectedValues()) {
                selectedItems.add((Item) obj);
            }
            
            setVisible(false);
        }
    }
    
    private class CancelAction extends AbstractAction {
        public CancelAction() {
            super("Cancel");
        }
        
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
        }
    }
}
