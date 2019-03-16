package dev.komu.kraken.utils

val d2 = Die(2)
val d3 = Die(3)
val d5 = Die(5)
val d4 = Die(4)
val d6 = Die(6)
val d7 = Die(7)
val d8 = Die(8)
val d9 = Die(9)
val d12 = Die(12)

class Die(val sides: Int) {
    operator fun plus(value: Int) = Expression.Die(1, sides) + value
}

operator fun Int.times(d: Die): Expression =
    Expression.Die(this, d.sides)

sealed class Expression {

    abstract fun evaluate(): Int

    operator fun plus(value: Int): Expression =
        Plus(this, constant(value))

    class RandomInt(private val range: ClosedRange<Int>) : Expression() {

        override fun evaluate(): Int = randomInt(range)
        override fun toString() = "randomInt($range)"
    }

    class Plus(private val lhs: Expression, private val rhs: Expression) : Expression() {

        override fun evaluate() = lhs.evaluate() + rhs.evaluate()
        override fun toString() = "$lhs+$rhs"
    }

    class Constant(private val value: Int) : Expression() {

        override fun evaluate() = value
        override fun toString() = value.toString()
    }

    class Die(private val multiplier: Int, private val sides: Int) : Expression() {

        override fun evaluate() = rollDie(sides, multiplier)
        override fun toString() = "${multiplier}d$sides"
    }

    companion object {

        fun constant(value: Int): Expression = Constant(value)
        fun random(range: ClosedRange<Int>): Expression = Expression.RandomInt(range)
    }
}
