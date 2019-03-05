package dev.komu.kraken.model.item.food

import dev.komu.kraken.model.common.Color
import dev.komu.kraken.model.creature.Player

class ColdCoffee: Food("potion of cold coffee") {

    init {
        letter = '!'
        color = Color.BLACK
    }

    override fun onEatenBy(eater: Player) {
        eater.charisma += 1
        eater.message("That hit the spot.")
    }
}
