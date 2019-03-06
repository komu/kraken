package dev.komu.kraken.model.actions

import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.item.Item

class PickupAction(private val item: Item, private val creature: Creature) : Action {
    override fun perform(): ActionResult {
        creature.inventory.add(item)
        creature.cell.items.remove(item)
        creature.message("Picked up %s.", item.title)
        return ActionResult.Success
    }
}
