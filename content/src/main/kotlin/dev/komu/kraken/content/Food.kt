package dev.komu.kraken.content

import dev.komu.kraken.model.common.Color
import dev.komu.kraken.model.creature.HungerLevel
import dev.komu.kraken.model.creature.Player
import dev.komu.kraken.model.item.Food

class ColdCoffee(name: String): Food(name) {

    init {
        letter = '!'
        color = Color.BLACK
    }

    override fun onEatenBy(eater: Player) {
        eater.charisma += 1
        eater.message("That hit the spot.")
    }
}

class CyanideCapsule(name: String): Food(name) {

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

class EyeOfBeholder(name: String): Food(name) {

    override fun onEatenBy(eater: Player) {
        eater.charisma += 10
        eater.message("You feel pretty.")
    }
}

class WaferThinMint(name: String): Food(name) {

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
