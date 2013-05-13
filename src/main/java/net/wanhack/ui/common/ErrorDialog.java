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
package net.wanhack.ui.common;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

public class ErrorDialog extends JDialog {
    
    private final Throwable throwable;

    public ErrorDialog(Frame parent, String title, Throwable throwable) {
        super(parent, title);

        this.throwable = throwable;
        
        setModal(true);
        setResizable(false);
        setLayout(new BorderLayout());

        add(createDetailPane(), BorderLayout.CENTER);
        add(createButtonPane(), BorderLayout.SOUTH);

        pack();
        
        setLocationRelativeTo(parent);
    }

    private JPanel createDetailPane() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        UIDefaults defaults = UIManager.getDefaults();
        Icon errorIcon = defaults.getIcon("OptionPane.errorIcon");

        JLabel icon = new JLabel(errorIcon, SwingConstants.LEFT);
        icon.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        panel.add(icon);
        
        JLabel text = new JLabel(throwable.toString());
        text.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        panel.add(text);

        Dimension preferred = panel.getPreferredSize();
        preferred.width = Math.max(preferred.width, 250);
        panel.setPreferredSize(preferred);

        return panel;
    }

    private JPanel createButtonPane() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton okButton = new JButton(new OkAction());
        getRootPane().setDefaultButton(okButton);
        
        panel.add(okButton);

        return panel;
    }

    private class OkAction extends AbstractAction {
        public OkAction() {
            super("Ok");
        }

        public void actionPerformed(ActionEvent e) {
            setVisible(false);
        }
    }

    public static void show(Frame frame, String title, Throwable exception) {
        new ErrorDialog(frame, title, exception).setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        String title = "Unexpected error";
        Exception ex = new RuntimeException("foobar", new IOException("barfoo"));
        
        ErrorDialog.show(null, title, ex);
    }
}