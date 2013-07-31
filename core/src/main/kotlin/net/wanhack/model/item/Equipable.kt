package net.wanhack.model.item

import net.wanhack.model.creature.Player

trait Equipable : Item {

    fun equip(player: Player): Boolean
}
