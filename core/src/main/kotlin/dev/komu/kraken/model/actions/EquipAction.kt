package dev.komu.kraken.model.actions

import dev.komu.kraken.model.creature.Player
import dev.komu.kraken.model.item.Equipable

class EquipAction(private val item: Equipable, private val player: Player) : Action {
    override fun perform(): ActionResult =
        if (item.equip(player)) ActionResult.Success else ActionResult.Failure
}
