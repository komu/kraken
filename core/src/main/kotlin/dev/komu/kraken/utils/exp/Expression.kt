package dev.komu.kraken.utils.exp

import dev.komu.kraken.utils.randomInt
import dev.komu.kraken.utils.rollDie

sealed class Expression {

    abstract fun evaluate(): Int

    operator fun plus(value: Int): Expression =
        Plus(this, constant(value))

    class RandomInt(private val range: ClosedRange<Int>) : Expression() {

        override fun evaluate(): Int =
            randomInt(range)

        override fun toString() =
            "randomInt($range)"
    }

    class Plus(private val lhs: Expression, private val rhs: Expression) :
        Expression() {

        override fun evaluate() = lhs.evaluate() + rhs.evaluate()
    }

    class Constant(private val value: Int) : Expression() {

        override fun evaluate() = value

        override fun toString() = value.toString()
    }

    class Die(private val multiplier: Int, private val sides: Int) : Expression() {

        override fun evaluate() =
            rollDie(sides, multiplier)

        override fun toString() = "${multiplier}d$sides"
    }

    companion object {

        fun constant(value: Int): Expression =
            Constant(value)

        fun random(range: ClosedRange<Int>): Expression =
            RandomInt(range)
    }
}
