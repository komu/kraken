package dev.komu.kraken.utils.exp

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

operator fun Int.times(d: Die): Expression = Expression.Die(this, d.sides)
