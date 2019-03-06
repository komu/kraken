package dev.komu.kraken.model.actions

import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.item.Item

class DropAction(private val item: Item, private val creature: Creature) : Action {
    override fun perform(): ActionResult {
        creature.inventory.remove(item)
        creature.cell.items.add(item)
        creature.message("Dropped %s.", item.title)
        return ActionResult.Success
    }
}
