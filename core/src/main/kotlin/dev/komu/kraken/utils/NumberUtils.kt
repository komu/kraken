package dev.komu.kraken.utils

fun countOfCellsAtDistance(distance: Int): Int {
    if (distance == 0)
        return 1

    val width = 2 * distance + 1
    return square(width) - square(width - 1)
}

fun square(n: Int) = n * n

fun signum(n: Int) =
    when {
        n < 0 -> -1
        n > 0 -> 1
        else  -> 0
    }
