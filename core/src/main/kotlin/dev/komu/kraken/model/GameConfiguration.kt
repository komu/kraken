package dev.komu.kraken.model

class GameConfiguration {

    var name = ""
        get() = if (field.isEmpty()) "Anonymous Coward" else field

    var pet = PetType.DORIS
    var wizardMode = false

    enum class PetType(val petName: String) {
        DORIS("Doris"),
        LASSIE("Lassie");

        override fun toString() = petName
    }
}
