package dev.komu.kraken.model.actions

import dev.komu.kraken.model.creature.Player
import dev.komu.kraken.model.item.food.Food

class EatAction(private val food: Food, private val player: Player) : Action {
    override fun perform(): ActionResult {
        player.inventory.remove(food)
        food.onEatenBy(player)
        return ActionResult.Success
    }
}
