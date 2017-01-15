package net.wanhack.model.item

import net.wanhack.model.creature.Player

abstract class Equipable(title: String) : Item(title) {

    abstract fun equip(player: Player): Boolean
}
