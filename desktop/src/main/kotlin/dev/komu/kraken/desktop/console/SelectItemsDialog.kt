package dev.komu.kraken.desktop.console

import com.jgoodies.forms.builder.DefaultFormBuilder
import com.jgoodies.forms.factories.ButtonBarFactory
import com.jgoodies.forms.layout.CellConstraints
import com.jgoodies.forms.layout.FormLayout
import dev.komu.kraken.desktop.extensions.makeAction
import dev.komu.kraken.desktop.extensions.set
import dev.komu.kraken.model.item.Item
import java.awt.Component
import java.awt.Frame
import java.util.*
import javax.swing.*

class SelectItemsDialog<T: Item>(owner: Frame, message: String, items: Collection<T>): JDialog() {

    private val itemList: JList<T> = JList(Vector(items))
    private var selectedItems = HashSet<T>()

    init {
        isModal = true

        itemList.cellRenderer = ItemCellRenderer()
        initContent()

        itemList.inputMap["ESCAPE"] = "cancel"
        itemList.actionMap["cancel"] = cancelAction()
        pack()
        setLocationRelativeTo(owner)
    }

    private fun initContent() {
        val cc = CellConstraints()
        val layout = FormLayout("pref", "pref, 8dlu, pref")
        val builder = DefaultFormBuilder(layout)
        builder.setDefaultDialogBorder()
        builder.add(JScrollPane(itemList))
        builder.add(createButtonBar(), cc.xy(1, 3))
        contentPane = builder.panel
    }

    private fun setAllowMultipleSelections(b: Boolean) {
        itemList.selectionMode = if (b) ListSelectionModel.MULTIPLE_INTERVAL_SELECTION else ListSelectionModel.SINGLE_SELECTION
    }

    private fun createButtonBar(): JPanel {
        val ok = JButton(makeAction("Ok") {
            selectedItems = LinkedHashSet<T>(itemList.selectedValuesList)
            isVisible = false
        })
        rootPane.defaultButton = ok

        return ButtonBarFactory.buildOKCancelBar(ok, JButton(cancelAction()))
    }

    private fun cancelAction() = makeAction("Cancel") {
        setVisible(false)
    }

    companion object {
        fun <T : Item> selectItems(frame: Frame, message: String, items: Collection<T>): Set<T> {
            val dlg = SelectItemsDialog(frame, message, items)
            dlg.setAllowMultipleSelections(true)
            dlg.isVisible = true
            return dlg.selectedItems
        }

        fun <T : Item> selectItem(frame: Frame, message: String, items: Collection<T>): T? {
            val dlg = SelectItemsDialog(frame, message, items)
            dlg.setAllowMultipleSelections(false)
            dlg.isVisible = true

            return dlg.selectedItems.firstOrNull()
        }

        class ItemCellRenderer : DefaultListCellRenderer() {

            override fun getListCellRendererComponent(list: JList<out Any>, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)

                val item = value as Item?

                text = item?.title ?: ""
                return this
            }
        }
    }
}
