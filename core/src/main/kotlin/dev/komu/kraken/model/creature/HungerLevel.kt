package dev.komu.kraken.model.creature

enum class HungerLevel(val min: Int) {
    SATIATED(2001),
    NOT_HUNGRY(301),
    HUNGRY(101),
    WEAK(1),
    FAINTING(0);

    val hungry: Boolean
        get() = min <= HUNGRY.min

    override fun toString(): String =
        if (this == NOT_HUNGRY)
            ""
        else
            name.toLowerCase()
}

fun Int.toHungerLevel(): HungerLevel =
    HungerLevel.values().firstOrNull { this >= it.min } ?: HungerLevel.FAINTING
