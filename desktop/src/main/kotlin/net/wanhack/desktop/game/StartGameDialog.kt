/*
 * Copyright 2013 The Wanhack Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.wanhack.desktop.game

import javax.swing.ButtonGroup
import javax.swing.JComboBox
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JRadioButton
import javax.swing.JTextField
import net.wanhack.model.GameConfiguration
import net.wanhack.model.GameConfiguration.PetType
import net.wanhack.model.creature.Sex
import net.wanhack.utils.SystemAccess
import com.jgoodies.forms.builder.DefaultFormBuilder
import com.jgoodies.forms.factories.ButtonBarFactory
import com.jgoodies.forms.layout.CellConstraints
import com.jgoodies.forms.layout.FormLayout
import kotlin.swing.*

class StartGameDialog(owner: JFrame): JDialog() {

    private var configuration: GameConfiguration? = null
    private val nameField = JTextField(20)
    private val maleRadio = JRadioButton("male", true)
    private val femaleRadio = JRadioButton("female", false)
    private val petCombo = JComboBox<PetType>(PetType.values());

    {
        setModal(true)
        initContent()
        val sexButtonGroup = ButtonGroup()
        sexButtonGroup.add(maleRadio)
        sexButtonGroup.add(femaleRadio)
        pack()
        setLocationRelativeTo(owner)
    }

    public fun showDialog(): GameConfiguration? {
        configuration = null
        nameField.setText(SystemAccess.getSystemProperty("name", ""))
        nameField.setSelectionStart(0)
        nameField.setSelectionEnd(nameField.getText()!!.length)
        setVisible(true)
        return configuration
    }

    private fun createConfiguration(): GameConfiguration {
        val config = GameConfiguration()
        config.name = nameField.getText()!!
        config.sex = if (maleRadio.isSelected()) Sex.MALE else Sex.FEMALE
        config.pet = petCombo.getSelectedItem() as PetType
        return config
    }

    private fun initContent() {
        val cc = CellConstraints()
        val layout = FormLayout("right:pref, 3dlu, pref", "pref, 3dlu, pref, pref, 3dlu, pref, 8dlu, pref")
        val builder = DefaultFormBuilder(layout)
        builder.setDefaultDialogBorder()
        builder.addLabel("Name", cc.xy(1, 1))
        builder.add(nameField, cc.xy(3, 1))
        builder.addLabel("Sex", cc.xy(1, 3))
        builder.add(maleRadio, cc.xy(3, 3))
        builder.add(femaleRadio, cc.xy(3, 4))
        builder.addLabel("Pet", cc.xy(1, 6))
        builder.add(petCombo, cc.xy(3, 6))
        builder.add(createButtonBar(), cc.xyw(1, 8, 3))
        setContentPane(builder.getPanel())
    }

    private fun createButtonBar(): JPanel {
        val ok = button("Ok") {
            configuration = createConfiguration()
            setVisible(false)
        }
        val cancel = button("Cancel") {
            configuration = null
            setVisible(false)
        }
        getRootPane()?.setDefaultButton(ok)
        return ButtonBarFactory.buildOKCancelBar(ok, cancel)
    }
}
