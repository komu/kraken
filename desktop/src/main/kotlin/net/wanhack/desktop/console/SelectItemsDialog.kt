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

package net.wanhack.desktop.console

import javax.swing.*
import java.awt.*
import java.util.LinkedHashSet
import java.util.HashSet
import com.jgoodies.forms.builder.DefaultFormBuilder
import com.jgoodies.forms.factories.ButtonBarFactory
import com.jgoodies.forms.layout.CellConstraints
import com.jgoodies.forms.layout.FormLayout
import net.wanhack.model.item.Item
import net.wanhack.desktop.extensions.set
import kotlin.swing.*

private class SelectItemsDialog(owner: Frame, message: String, items: Collection<Item>): JDialog() {

    private val itemList: JList<Item>
    private var selectedItems = HashSet<Item>();

    {
        setModal(true)

        itemList = JList<Item>(items.toArray(Array<Item>(0) { throw Exception("unexpected") }))
        itemList.setCellRenderer(ItemCellRenderer())
        initContent()

        val inputMap = itemList.getInputMap()!!
        val actionMap = itemList.getActionMap()!!
        inputMap["ESCAPE"] = "cancel"
        actionMap["cancel"] = cancelAction()
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
        setContentPane(builder.getPanel())
    }

    private fun setAllowMultipleSelections(b: Boolean) {
        itemList.setSelectionMode(if (b) ListSelectionModel.MULTIPLE_INTERVAL_SELECTION else ListSelectionModel.SINGLE_SELECTION)
    }

    private fun createButtonBar(): JPanel {
        val ok = button("Ok") {
            selectedItems = LinkedHashSet<Item>(itemList.getSelectedValuesList())
            setVisible(false)
        }
        getRootPane()!!.setDefaultButton(ok)

        return ButtonBarFactory.buildOKCancelBar(ok, JButton(cancelAction()))
    }

    private fun cancelAction() = action("Cancel") {
        setVisible(false)
    }

    class object {
        public open fun selectItems(frame: Frame, message: String, items: Collection<Item>): MutableSet<Item> {
            val dlg = SelectItemsDialog(frame, message, items)
            dlg.setAllowMultipleSelections(true)
            dlg.setVisible(true)
            return dlg.selectedItems
        }

        public open fun selectItem(frame: Frame, message: String, items: Collection<Item>): Item? {
            val dlg = SelectItemsDialog(frame, message, items)
            dlg.setAllowMultipleSelections(false)
            dlg.setVisible(true)

            val selected = dlg.selectedItems
            return if (!selected.empty) selected.first() else null
        }

        class ItemCellRenderer : DefaultListCellRenderer() {

            public override fun getListCellRendererComponent(list: JList<out Any>, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)

                val item = value as Item?

                setText(item?.title ?: "")
                return this
            }
        }
    }
}
