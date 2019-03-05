package dev.komu.kraken.utils.exp

import dev.komu.kraken.utils.rollDie

class DieExpression(val multiplier: Int, val sides: Int): Expression() {

    override fun evaluate(env: Map<String, Int>) =
        rollDie(sides, multiplier)

    override fun toString() = "${multiplier}d$sides"
}
