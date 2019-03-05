package dev.komu.kraken.model.item

import dev.komu.kraken.model.creature.Player

abstract class Equipable(title: String) : Item(title) {

    abstract fun equip(player: Player): Boolean
}
