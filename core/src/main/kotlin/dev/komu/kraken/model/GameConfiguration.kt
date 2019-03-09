package dev.komu.kraken.model

import org.jetbrains.annotations.TestOnly

class GameConfiguration(
    val name: String,
    val pet: PetType = PetType.LASSIE
){

    var wizardMode = false

    enum class PetType(private val petName: String) {
        DORIS("Doris"),
        LASSIE("Lassie");

        override fun toString() = petName
    }

    companion object {
        @TestOnly
        fun dummy() = GameConfiguration("Bilbo Baggings")
    }
}
