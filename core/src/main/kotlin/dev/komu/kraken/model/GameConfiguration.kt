package dev.komu.kraken.model

import dev.komu.kraken.model.creature.Sex
import dev.komu.kraken.utils.randomEnum

class GameConfiguration {

    var name = ""
        get() = if (field.isEmpty()) "Anonymous Coward" else field

    var sex = randomEnum<Sex>()
    var pet = PetType.DORIS
    var wizardMode = false

    enum class PetType(val petName: String) {
        DORIS("Doris"),
        LASSIE("Lassie");

        override fun toString() = petName
    }
}
