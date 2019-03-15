package dev.komu.kraken.model

import dev.komu.kraken.model.creature.PetType
import org.jetbrains.annotations.TestOnly

class GameConfiguration(
    val name: String,
    val pet: PetType = PetType.LASSIE
){

    var wizardMode = false

    companion object {
        @TestOnly
        fun dummy() = GameConfiguration("Bilbo Baggings")
    }
}
