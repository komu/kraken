package dev.komu.kraken.desktop.game

import com.jgoodies.forms.builder.DefaultFormBuilder
import com.jgoodies.forms.factories.ButtonBarFactory
import com.jgoodies.forms.layout.CellConstraints
import com.jgoodies.forms.layout.FormLayout
import dev.komu.kraken.content.PetType
import dev.komu.kraken.desktop.extensions.makeAction
import dev.komu.kraken.model.GameConfiguration
import javax.swing.*

class StartGameDialog(owner: JFrame) : JDialog() {

    private var configuration: GameConfiguration? = null
    private val nameField = JTextField(20)
    private val petCombo = JComboBox<PetType>(PetType.values())

    init {
        isModal = true
        initContent()
        pack()
        setLocationRelativeTo(owner)
    }

    fun showDialog(): GameConfiguration? {
        configuration = null
        nameField.text = System.getProperty("name", "")
        nameField.selectionStart = 0
        nameField.selectionEnd = nameField.text.length
        isVisible = true
        return configuration
    }

    private fun createConfiguration(): GameConfiguration {
        val pet = petCombo.selectedItem as PetType
        return GameConfiguration(
            name = nameField.text.takeUnless { it.isEmpty() } ?: "Anonymous Coward",
            pet = pet::instantiate)
    }

    private fun initContent() {
        val cc = CellConstraints()
        val layout = FormLayout("right:pref, 3dlu, pref", "pref, 3dlu, pref, 8dlu, pref")
        val builder = DefaultFormBuilder(layout)
        builder.setDefaultDialogBorder()
        builder.addLabel("Name", cc.xy(1, 1))
        builder.add(nameField, cc.xy(3, 1))
        builder.addLabel("Pet", cc.xy(1, 3))
        builder.add(petCombo, cc.xy(3, 3))
        builder.add(createButtonBar(), cc.xyw(1, 5, 3))
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
