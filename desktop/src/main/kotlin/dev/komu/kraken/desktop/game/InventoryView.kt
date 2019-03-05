/*
 * Copyright 2013 The Releasers of Kraken
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

package dev.komu.kraken.desktop.game

import dev.komu.kraken.model.item.ItemInfo
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.util.*
import javax.swing.*

class InventoryView : JPanel(BorderLayout()) {
    private val list = JList<ItemInfo>()

    init {
        list.selectionMode = ListSelectionModel.SINGLE_SELECTION
        list.cellRenderer = InventoryCellRenderer()
        list.isFocusable = false
        list.background = Color.BLACK
        preferredSize = Dimension(200, 200)
        val scrollPane = JScrollPane(list)
        scrollPane.border = BorderFactory.createEmptyBorder()
        add(scrollPane, BorderLayout.CENTER)
    }

    fun update(items: List<ItemInfo>) {
        val infos = Vector<ItemInfo>(items)

        Collections.sort(infos)

        SwingUtilities.invokeLater {
            list.setListData(infos)
        }
    }
}
