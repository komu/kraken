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

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.ArrayList
import java.util.Collections
import java.util.Vector
import javax.swing.BorderFactory
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.ListSelectionModel
import net.wanhack.desktop.game.action.DropItemAction
import javax.swing.SwingUtilities
import kotlin.swing.*
import net.wanhack.model.ReadOnlyGame
import net.wanhack.model.GameFacade
import net.wanhack.model.item.ItemInfo

class InventoryView : JPanel(BorderLayout()) {
    private val list = JList<ItemInfo>();

    {
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        list.setCellRenderer(InventoryCellRenderer())
        list.setFocusable(false)
        list.setBackground(Color.BLACK)
        setPreferredSize(Dimension(200, 200))
        val scrollPane = JScrollPane(list)
        scrollPane.setBorder(BorderFactory.createEmptyBorder())
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
