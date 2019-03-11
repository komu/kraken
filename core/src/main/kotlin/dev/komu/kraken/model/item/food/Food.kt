package dev.komu.kraken.model.item.food

import dev.komu.kraken.model.creature.Player
import dev.komu.kraken.model.item.Item

open class Food(name: String): Item(name) {

    var effectiveness = 100
    var healingEffect = 0

    init {
        letter = '%'
    }

    open fun onEatenBy(eater: Player) {
        eater.decreaseHungriness(effectiveness)
        eater.message("This %s is delicious!", title)

        if (healingEffect != 0) {
            eater.hitPoints = Math.min(eater.hitPoints + healingEffect, eater.maximumHitPoints)
            eater.message("You feel better.")
        }
    }
}
