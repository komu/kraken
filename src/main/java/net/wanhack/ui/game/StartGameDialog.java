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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import net.wanhack.model.GameConfiguration;
import net.wanhack.model.GameConfiguration.PetType;
import net.wanhack.model.creature.Sex;
import net.wanhack.utils.SystemAccess;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StartGameDialog extends JDialog {
    
    private GameConfiguration configuration = null;
    private final JTextField nameField = new JTextField(20);
    private final JRadioButton maleRadio = new JRadioButton("male", true);
    private final JRadioButton femaleRadio = new JRadioButton("female", false);
    private final JComboBox petCombo = new JComboBox(PetType.values());
    
    public StartGameDialog(JFrame owner) {
        super(owner, "Start Game");
        setModal(true);

        initContent();
        
        ButtonGroup sexButtonGroup = new ButtonGroup();
        sexButtonGroup.add(maleRadio);
        sexButtonGroup.add(femaleRadio);
        
        pack();
        setLocationRelativeTo(owner);
    }
    
    public GameConfiguration showDialog() {
        configuration = null;
        
        nameField.setText(SystemAccess.getSystemProperty("name", ""));
        nameField.setSelectionStart(0);
        nameField.setSelectionEnd(nameField.getText().length());
        
        setVisible(true);
        return configuration;
    }

    private GameConfiguration getGameConfiguration() {
        GameConfiguration config = new GameConfiguration();
        config.setName(nameField.getText());
        config.setSex(maleRadio.isSelected() ? Sex.MALE : Sex.FEMALE);
        config.setPet((PetType) petCombo.getSelectedItem());
        return config;
    }

    private void initContent() {
        CellConstraints cc = new CellConstraints();
        
        FormLayout layout = new FormLayout(
                "right:pref, 3dlu, pref", 
                "pref, 3dlu, pref, pref, 3dlu, pref, 8dlu, pref");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setDefaultDialogBorder();

        builder.addLabel("Name",        cc.xy(1, 1));
        builder.add(nameField,          cc.xy(3, 1));
        
        builder.addLabel("Sex",         cc.xy(1, 3));
        builder.add(maleRadio,          cc.xy(3, 3));
        builder.add(femaleRadio,        cc.xy(3, 4));
        
        builder.addLabel("Pet",         cc.xy(1, 6));
        builder.add(petCombo,           cc.xy(3, 6));
        
        builder.add(createButtonBar(),  cc.xyw(1, 8, 3));
        
        setContentPane(builder.getPanel());
    }
    
    private JPanel createButtonBar() {
        JButton ok = new JButton(new OkAction());
        JButton cancel = new JButton(new CancelAction());
        
        getRootPane().setDefaultButton(ok);
        
        return ButtonBarFactory.buildOKCancelBar(ok, cancel);
    }
    
    private class OkAction extends AbstractAction {
        public OkAction() {
            super("Ok");
        }
        
        public void actionPerformed(ActionEvent e) {
            configuration = getGameConfiguration(); 
            setVisible(false);
        }
    }

    private class CancelAction extends AbstractAction {
        public CancelAction() {
            super("Cancel");
        }
        
        public void actionPerformed(ActionEvent e) {
            configuration = null;
            setVisible(false);
        }
    }
}
