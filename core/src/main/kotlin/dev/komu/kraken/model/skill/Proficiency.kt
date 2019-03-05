package dev.komu.kraken.model.skill

import dev.komu.kraken.utils.square

enum class Proficiency(val level: Int, val bonus: Int) {
    UNSKILLED(1, -2),
    BASIC(2, 0),
    SKILLED(3, 1),
    EXPERT(4, 2),
    MASTER(5, 3) ,
    GRAND_MASTER(6, 5);

    val next: Proficiency?
        get() = values().elementAtOrNull(ordinal + 1)

    val trainingToReachThisLevel = square(level - 1) * 20

    override fun toString() =
        if (this == GRAND_MASTER)
            "grand master"
        else
            name.toLowerCase()
}
