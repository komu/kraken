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

        infos.sort()

        SwingUtilities.invokeLater {
            list.setListData(infos)
        }
    }
}
