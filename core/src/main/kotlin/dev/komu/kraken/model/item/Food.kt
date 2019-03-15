package dev.komu.kraken.model.item

import dev.komu.kraken.model.creature.Player

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

enum class Taste(private val s: String, private val like: Boolean) {
    APPLE("apple", true),
    CHICKEN("chicken", true),
    STRAWBERRY("strawberries", true),
    BLUEBERRY("blueberries", true),
    ELDERBERRY("elderberries", true),
    VANILLA("vanilla", true),
    CHEESE("cheese", true),
    STRANGE("strange", false),
    DULL("dull", false);

    override fun toString() = if (like) "like $s" else s
}
