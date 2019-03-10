package dev.komu.kraken.model.item.food

import dev.komu.kraken.model.creature.Player
import dev.komu.kraken.utils.exp.Expression
import dev.komu.kraken.utils.rollDie

class Corpse(name: String): Food(name) {

    var poisonDamage: Expression? = null
    var taste = Taste.CHICKEN

    override fun onEatenBy(eater: Player) {
        eater.decreaseHungriness(effectiveness)
        val poisonDamage = calculatePoisonDamage()
        if (poisonDamage > 0) {
            eater.message("This %s tastes terrible, it must have been poisonous!", title)
            eater.takeDamage(poisonDamage, eater, cause = "poisonous corpse")
        } else {
            eater.message("This %s tastes %s.", title, taste)
        }
    }

    private fun calculatePoisonDamage(): Int {
        val baseDamage = poisonDamage?.evaluate() ?: 0
        return if (baseDamage > 0)
            rollDie(baseDamage * level)
        else
            0
    }
}
