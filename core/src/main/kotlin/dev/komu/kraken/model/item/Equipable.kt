package dev.komu.kraken.model.item

import dev.komu.kraken.model.creature.Creature

abstract class Equipable(title: String) : Item(title) {

    abstract fun equip(creature: Creature): Boolean
}
