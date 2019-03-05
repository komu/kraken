package dev.komu.kraken.model.item.food

import dev.komu.kraken.model.creature.Player

class CyanideCapsule: Food("a cyanide capsule") {

    override fun onEatenBy(eater: Player) {
        if (eater.ask(false, "Really take %s?", title)) {
            eater.message("You swallow %s.", title)
            eater.die(title)
        } else {
            eater.message("You change your mind.")
            eater.inventory.add(this)
        }
    }
}
