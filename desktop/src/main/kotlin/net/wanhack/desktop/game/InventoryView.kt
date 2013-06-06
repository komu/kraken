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

class InventoryView : JPanel(BorderLayout()) {
    private val list = JList<ItemInfo>()
    var gameFacade: GameFacade? = null;

    {
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        list.setCellRenderer(InventoryCellRenderer())
        list.setFocusable(false)
        list.addMouseListener(ListMouseListener())
        list.setBackground(Color.BLACK)
        setPreferredSize(Dimension(200, 200))
        val scrollPane = JScrollPane(list)
        scrollPane.setBorder(BorderFactory.createEmptyBorder())
        add(scrollPane, BorderLayout.CENTER)
    }

    fun update(game: ReadOnlyGame?) {
        if (game == null) return

        val player = game.player
        val inventory = player.inventoryItems
        val infos = ArrayList<ItemInfo>(inventory.size + 10)

        for (item in inventory)
            infos.add(ItemInfo(item, false))

        for (item in player.activatedItems)
            infos.add(ItemInfo(item, true))

        Collections.sort(infos)

        SwingUtilities.invokeLater {
            list.setListData(Vector<ItemInfo>(infos))
        }
    }

    private inner class ListMouseListener(): MouseAdapter() {
        public override fun mousePressed(e: MouseEvent) {
            if (e.isPopupTrigger())
                showPopupMenu(e)
        }

        public override fun mouseReleased(e: MouseEvent) {
            if (e.isPopupTrigger())
                showPopupMenu(e)
        }

        private fun showPopupMenu(e: MouseEvent) {
            if (list.isSelectionEmpty()) {
                val index = list.locationToIndex(e.getPoint())
                if (index != -1)
                    list.setSelectedIndex(index)
            }

            val item = list.getSelectedValue()
            if (item != null) {
                val popup = popupMenu {
                    if (!item.inUse)
                        add(DropItemAction(gameFacade, item.item))
                }
                popup.show(list, e.getX(), e.getY())
            }
        }
    }
}
