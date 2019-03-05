package dev.komu.kraken.utils

import java.util.*

private val random = Random()

inline fun <reified T: Enum<T>> randomEnum(): T =
    T::class.java.enumConstants.toList().randomElement()

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
fun randomInt(min: Int, max:Int) = min + random.nextInt(max - min + 1)

fun MutableList<*>.shuffle() {
    shuffle(random)
}

fun <T> Collection<T>.shuffled(): MutableList<T> {
    val result = this.toMutableList()
    result.shuffle()
    return result
}

fun <T> List<T>.randomElement(): T =
        this[randomInt(this.size)]

fun <T> Array<T>.randomElement(): T =
        this[randomInt(this.size)]
