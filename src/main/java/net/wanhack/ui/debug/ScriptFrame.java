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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.Writer;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.wanhack.model.IGame;

import org.python.util.InteractiveInterpreter;


public class ScriptFrame extends JFrame {

    private final JTextArea textArea = new JTextArea();
    private final InteractiveInterpreter interpreter = new InteractiveInterpreter();
    private final JTextField inputField = new JTextField(60);
    private final TextAreaWriter writer = new TextAreaWriter();
    
    public ScriptFrame() {
        super("Scripting Console");
        
        interpreter.setOut(writer);
        interpreter.setErr(writer);
        
        textArea.setFocusable(false);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setColumns(80);
        textArea.setRows(15);

        getContentPane().setLayout(new BorderLayout());
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(createInputBar(), BorderLayout.SOUTH);
        
        setLocationByPlatform(true);
        pack();
    }

    private JPanel createInputBar() {
        JButton executeButton = new JButton(new ExecuteAction());
        getRootPane().setDefaultButton(executeButton);

        JPanel panel = new JPanel(new FlowLayout());
        panel.add(inputField);
        panel.add(executeButton);
        
        return panel;
    }
    
    private void execute(String code) {
        try {
            boolean fail = interpreter.runsource(code);
            if (fail) {
                writer.write("error: incomplete input");
            }
        } catch (Exception e) {
            writer.write(e.toString());
        }
    }

    public void setGame(IGame game) {
        interpreter.set("game", game);
        
        if (game != null) {
            interpreter.set("player", game.getPlayer());
        }
    }
    
    private class ExecuteAction extends AbstractAction {
        public ExecuteAction() {
            super("Execute");
        }
        
        public void actionPerformed(ActionEvent e) {
            String code = inputField.getText();
            inputField.setText("");
            
            if (!code.equals("")) {
                execute(code);
            }
        }
    }
    
    private class TextAreaWriter extends Writer {
        @Override
        public void write(String str) {
            textArea.setText(textArea.getText() + str);
        }
        
        @Override
        public void write(String str, int off, int len) {
            write(str.substring(off, off + len));
        }
        
        @Override
        public void write(char[] cbuf, int off, int len) {
            write(new String(cbuf, off, len));
        }
        
        @Override
        public void flush() {
        }
        
        @Override
        public void close() {
        }
    }
}
