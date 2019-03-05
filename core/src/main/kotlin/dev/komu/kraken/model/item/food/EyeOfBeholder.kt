package dev.komu.kraken.model.item.food

import dev.komu.kraken.model.creature.Player

class EyeOfBeholder(name: String): Food(name) {

    override fun onEatenBy(eater: Player) {
        eater.charisma += 10
        eater.message("You feel pretty.")
    }
}
