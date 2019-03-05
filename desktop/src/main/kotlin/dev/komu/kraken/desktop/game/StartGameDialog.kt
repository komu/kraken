package dev.komu.kraken.desktop.game

import com.jgoodies.forms.builder.DefaultFormBuilder
import com.jgoodies.forms.factories.ButtonBarFactory
import com.jgoodies.forms.layout.CellConstraints
import com.jgoodies.forms.layout.FormLayout
import dev.komu.kraken.desktop.extensions.makeAction
import dev.komu.kraken.model.GameConfiguration
import dev.komu.kraken.model.GameConfiguration.PetType
import dev.komu.kraken.model.creature.Sex
import dev.komu.kraken.utils.SystemAccess
import javax.swing.*

class StartGameDialog(owner: JFrame): JDialog() {

    private var configuration: GameConfiguration? = null
    private val nameField = JTextField(20)
    private val maleRadio = JRadioButton("male", true)
    private val femaleRadio = JRadioButton("female", false)
    private val petCombo = JComboBox<PetType>(PetType.values())

    init {
        isModal = true
        initContent()
        val sexButtonGroup = ButtonGroup()
        sexButtonGroup.add(maleRadio)
        sexButtonGroup.add(femaleRadio)
        pack()
        setLocationRelativeTo(owner)
    }

    fun showDialog(): GameConfiguration? {
        configuration = null
        nameField.text = SystemAccess.getSystemProperty("name", "")
        nameField.selectionStart = 0
        nameField.selectionEnd = nameField.text.length
        isVisible = true
        return configuration
    }

    private fun createConfiguration(): GameConfiguration {
        val config = GameConfiguration()
        config.name = nameField.text
        config.sex = if (maleRadio.isSelected) Sex.MALE else Sex.FEMALE
        config.pet = petCombo.selectedItem as PetType
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
        contentPane = builder.panel
    }

    private fun createButtonBar(): JPanel {
        val ok = JButton(makeAction("Ok") {
            configuration = createConfiguration()
            isVisible = false
        })
        val cancel = JButton(makeAction("Cancel") {
            configuration = null
            isVisible = false
        })
        rootPane.defaultButton = ok
        return ButtonBarFactory.buildOKCancelBar(ok, cancel)
    }
}
