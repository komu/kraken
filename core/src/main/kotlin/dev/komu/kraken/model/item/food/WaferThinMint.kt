package dev.komu.kraken.model.item.food

import dev.komu.kraken.model.creature.HungerLevel
import dev.komu.kraken.model.creature.Player

class WaferThinMint: Food("wafer-thin mint") {

    init {
        weight = 20
    }

    override fun onEatenBy(eater: Player) {
        if (eater.hungerLevel == HungerLevel.SATIATED) {
            eater.hitPoints = 0
            eater.message("This %s is too much. %s %s!", title, eater.You(), eater.verb("explode"))
            eater.die(title)
        } else {
            eater.decreaseHungriness(1)
            eater.message("What a delicious %s!", title)
        }
    }
}
