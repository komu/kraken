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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.util.Collection;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import net.wanhack.model.common.Console;
import net.wanhack.model.common.Direction;
import net.wanhack.model.item.Item;
import net.wanhack.utils.collections.CircularBuffer;


public class ConsoleView extends JComponent implements Console {

    private final CircularBuffer<String> buffer = new CircularBuffer<String>(100);
    private boolean hasOutputOnThisTurn = false;
    private int currentRow = 0;
    private static final int ROWS_TO_SHOW = 3;
    
    public ConsoleView() {
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        setFont(new Font("Monospaced", Font.PLAIN, 14));
    }
    
    public boolean ask(String format, Object... args) {
        String question = String.format(format, args);
        Frame frame = JOptionPane.getFrameForComponent(this);
        int result = JOptionPane.showConfirmDialog(
                frame, question, "Question", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        return result == JOptionPane.YES_OPTION;
    }
    
    public synchronized void scrollUp() {
        currentRow = Math.max(0, currentRow - 1);
        repaint();
    }
    
    public synchronized void scrollDown() {
        currentRow = Math.min(buffer.size() - 1, currentRow + 1);
        repaint();
    }
    
    @Override
    public synchronized void paint(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        
        if (!buffer.isEmpty() && currentRow < buffer.size()) {
            g.setColor(getForeground());
            FontMetrics fm = getFontMetrics(getFont());
            int maxWidth = getWidth();
            
            String text = buffer.get(currentRow);
            String[] words = text.split(" ");
            int x = 0;
            int y = fm.getAscent();
            for (int i = 0; i < words.length; i++) {
                int wordWidth = fm.stringWidth(words[i]);
                if (x != 0 && x + wordWidth >= maxWidth) {
                    x = 0;
                    y += fm.getHeight();
                }
                
                g.drawString(words[i], x, y);
                x += wordWidth + fm.charWidth(' ');
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        return new Dimension(200, ROWS_TO_SHOW * fm.getHeight());
    }
    
    public synchronized void turnEnd() {
        if (!hasOutputOnThisTurn) {
            if (!buffer.isEmpty() && !"".equals(buffer.last())) {
                buffer.add("");
            }
        }
        hasOutputOnThisTurn = false;
        currentRow = buffer.size() - 1;
        repaint();
    }
    
    public synchronized void message(String message) {
        if (hasOutputOnThisTurn) {
            String last = buffer.last();
            buffer.replaceLast(last + " " + message);
        } else {
            if (!buffer.isEmpty() && "".equals(buffer.last())) {
                buffer.replaceLast(message);
            } else {
                buffer.add(message);
            }
            hasOutputOnThisTurn = true;
        }
    }
    
    public Direction selectDirection() {
        Frame frame = JOptionPane.getFrameForComponent(this);
        return SelectDirectionDialog.selectDirection(frame);
    }
    
    public <T extends Item> T selectItem(Class<T> type, String message, Collection<? extends T> items) {
        Frame frame = JOptionPane.getFrameForComponent(this);
        return type.cast(SelectItemsDialog.selectItem(frame, message, items));
    }
    
    public Set<Item> selectItems(String message, Collection<? extends Item> items) {
        Frame frame = JOptionPane.getFrameForComponent(this);
        return SelectItemsDialog.selectItems(frame, message, items);
    }

    public synchronized void clear() {
        buffer.clear();
        hasOutputOnThisTurn = false;
    }
}
