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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import net.wanhack.model.IGame;
import net.wanhack.model.creature.HungerLevel;
import net.wanhack.model.creature.Player;

/**
 * View for showing the statistics lines.
 */
public final class StatisticsView extends JComponent {

    private Line line1 = null;
    private Line line2 = null;
    
    public StatisticsView() {
        setBackground(Color.BLACK);
        setBorder(null);
        setFont(new Font("Monospaced", Font.PLAIN, 14));
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(300, 2 * getFontMetrics(getFont()).getHeight());
    }
    
    @Override
    public void paint(Graphics g) {
        final Graphics2D g2 = (Graphics2D) g;
        
        g2.setPaint(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setFont(getFont());

        FontMetrics fm = g2.getFontMetrics();

        int y1 = fm.getAscent();
        int y2 = fm.getAscent() * 2;
        
        drawLine(g2, line1, y1);
        drawLine(g2, line2, y2);
    }
    
    private void drawLine(Graphics2D g, Line line, int y) {
        if (line == null) return;

        FontMetrics fm = g.getFontMetrics();
        
        int x = 0;
        for (TextFragment fragment : line.fragments) {
            g.setColor(fragment.color);
            g.drawString(fragment.text, x, y);
            
            x += fm.stringWidth(fragment.text);
        }
    }

    public void updateStatistics(IGame game) {
        line1 = getStatsLine1(game);
        line2 = getStatsLine2(game);
        
        repaint();
    }

    private Line getStatsLine1(IGame game) {
        if (game == null) return null;
        
        Player player = game.getPlayer();
        
        Line line = new Line();
        line.add("%-20s  St:%d Ch:%d",
                 player.getName(),
                 player.getStrength(),
                 player.getCharisma());
        return line;
    }
    
    private Line getStatsLine2(IGame game) {
        if (game == null) return null;
        
        Player player = game.getPlayer();
        
        Line line = new Line();
        
        line.add("%-20s  ", player.getRegion().getTitle());
        line.add(getHitpointsColor(player), "HP:%d(%d)",
                 player.getHitpoints(),
                 player.getMaximumHitpoints());
        line.add("  AC:%d  Exp:%s(%s)  T:%d    ", 
                 player.getArmorClass(),
                 player.getLevel(),
                 player.getExperience(),
                 game.getTime());
        
        line.add(getHungerColor(player.getHungerLevel()), 
                 "%-10s", player.getHungerLevel());
        
        return line;
    }

    private static Color getHitpointsColor(Player player) {
        if (player.getHitpoints() <= player.getMaximumHitpoints() / 4) {
            return Color.RED;
        } else {
            return Color.WHITE;
        }
    }
    
    private static Color getHungerColor(HungerLevel level) {
        return level.isHungry() ? Color.RED : Color.WHITE;
    }
    
    private static class Line {
        private final List<TextFragment> fragments = new ArrayList<TextFragment>();
       
        public void add(String format, Object... args) {
            fragments.add(new TextFragment(format, args));
        }
        
        public void add(Color color, String format, Object... args) {
            fragments.add(new TextFragment(color, format, args));
        }
    }
    
    private static class TextFragment {
        private final String text;
        private final Color color;
        
        public TextFragment(String format, Object... args) {
            this.text = String.format(format, args);
            this.color = Color.WHITE;
        }
        
        public TextFragment(Color color, String format, Object... args) {
            this.text = String.format(format, args);
            this.color = color;
        }
    }
}
