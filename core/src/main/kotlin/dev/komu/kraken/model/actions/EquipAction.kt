package dev.komu.kraken.model.actions

import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.item.Equipable

class EquipAction(private val item: Equipable, private val creature: Creature) : Action {
    override fun perform(): ActionResult =
        if (item.equip(creature)) ActionResult.Success else ActionResult.Failure
}
