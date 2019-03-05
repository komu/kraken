package dev.komu.kraken.model.item

class ItemInfo(val title: String, val description: String, val letter: Char, val inUse: Boolean): Comparable<ItemInfo> {
    override fun compareTo(other: ItemInfo) =
        if (inUse != other.inUse)
            if (inUse) -1 else 1
        else
            title.compareTo(other.title)
}
