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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public final class InventoryCellRenderer extends JComponent implements ListCellRenderer {
    
    private ItemInfo item;
    private String title = "";
    private String description = "";
    private final Font titleFont = new Font("SansSerif", Font.BOLD, 13);
    private final Font descriptionFont = new Font("SansSerif", Font.PLAIN, 11);

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm1 = getFontMetrics(titleFont);
        FontMetrics fm2 = getFontMetrics(descriptionFont);

        int left = Math.max(fm1.stringWidth(" % "), fm1.stringWidth(" M "));
        
        int width = left + Math.max(fm1.stringWidth(title), fm2.stringWidth(description));
        return new Dimension(width, fm1.getHeight() + fm2.getHeight());
    }

    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        this.item = (ItemInfo) value;
        this.title = item.getTitle();
        this.description = item.getDescription();

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        
        if (cellHasFocus) {
            setBorder(UIManager.getBorder("List.focusCellHighlightBorder"));
        } else {
            setBorder(new EmptyBorder(1, 1, 1, 1));
        }
        
        return this;
    }
    
    @Override
    public void paint(Graphics g) {
        // Background
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        // Foreground
        g.setColor(item.isInUse() ? Color.WHITE : Color.LIGHT_GRAY);
        
        FontMetrics fm1 = getFontMetrics(titleFont);
        FontMetrics fm2 = getFontMetrics(descriptionFont);
        int left = Math.max(fm1.stringWidth(" % "), fm1.stringWidth(" M "));

        g.setFont(titleFont);
        
        g.drawString(String.valueOf(item.getLetter()), 2, fm1.getAscent());
        g.drawString(title, left, fm1.getAscent());
        
        g.setFont(descriptionFont);
        g.drawString(description, left, fm1.getHeight() + fm2.getAscent());
    }
}