package dev.komu.kraken.model.item.food

import dev.komu.kraken.model.creature.Player

class HealingEdible(title: String): Food(title) {

    var healingEffect = 1

    override fun onEatenBy(eater: Player) {
        super.onEatenBy(eater)
        eater.hitPoints = Math.min(eater.hitPoints + healingEffect, eater.maximumHitPoints)
        eater.message("You feel better.")
    }
}
