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
package net.wanhack.ui.debug;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

public class LogFrame extends JFrame {

    private final JTextArea textArea = new JTextArea();
    private final FrameLogHandler handler = new FrameLogHandler();
    private final Logger logger = Logger.getLogger("net.wanhack");
    
    public LogFrame() {
        super("Log");

        logger.setLevel(Level.INFO);
        logger.addHandler(handler);
        
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setColumns(80);
        textArea.setRows(15);

        getContentPane().setLayout(new BorderLayout());
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(createToolBar(), BorderLayout.NORTH);
        
        setLocationByPlatform(true);
        pack();
    }
    
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        toolBar.add(new ClearLogAction());
        toolBar.addSeparator();
        toolBar.add(createLogLevelSwitcher());
        
        return toolBar;
    }
    
    private JComponent createLogLevelSwitcher() {
        Level[] levels = { 
            Level.ALL, 
            Level.FINEST, Level.FINER, Level.FINE, Level.CONFIG, 
            Level.INFO, Level.WARNING, Level.SEVERE, 
            Level.OFF
        };
        
        final JComboBox comboBox = new JComboBox(levels);
        comboBox.setSelectedItem(logger.getLevel());
        comboBox.setPrototypeDisplayValue("WARNING");
        comboBox.setMaximumRowCount(10);
        comboBox.setMaximumSize(comboBox.getPreferredSize());
        
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Level level = (Level) comboBox.getSelectedItem();
                logger.setLevel(level);
            }
        });
        
        return comboBox;
    }
    
    private class FrameLogHandler extends Handler {
        private final Formatter formatter = new SimpleFormatter();
        
        @Override
        public void publish(LogRecord record) {
            if (isLoggable(record)) {
                String formatted = formatter.format(record);
                textArea.setText(textArea.getText() + formatted);
            }
        }
        
        @Override
        public void flush() {
        }
        
        @Override
        public void close() {
        }
    }
    
    private class ClearLogAction extends AbstractAction {
        public ClearLogAction() {
            super("Clear");
        }
        
        public void actionPerformed(ActionEvent e) {
            textArea.setText("");
        }
    }
}
