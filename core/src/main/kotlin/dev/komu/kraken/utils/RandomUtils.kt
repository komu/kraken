package dev.komu.kraken.utils

import java.util.*

private val random = Random()

fun <T> randomItem(vararg items: T): T =
    items.randomElement()

fun rollDie(sides: Int, times: Int = 1): Int {
    var total = 0

    repeat(times) {
        total += 1 + random.nextInt(sides)
    }

    return total
}

fun randomInt(n: Int): Int = random.nextInt(n)
fun randomInt(min: Int, max: Int) = min + random.nextInt(max - min + 1)
fun randomInt(range: ClosedRange<Int>) = range.start + random.nextInt(range.endInclusive - range.start + 1)

fun <T> List<T>.randomElement(): T =
    this[randomInt(size)]

fun <T> Array<T>.randomElement(): T =
    this[randomInt(size)]
