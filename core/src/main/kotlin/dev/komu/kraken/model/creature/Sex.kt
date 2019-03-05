package dev.komu.kraken.model.creature

enum class Sex {
    MALE,
    FEMALE;

    val letter = Character.toLowerCase(name[0])

    override fun toString() = name.toLowerCase()
}
