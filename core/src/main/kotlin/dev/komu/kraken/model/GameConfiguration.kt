package dev.komu.kraken.model

import dev.komu.kraken.model.creature.Monster
import org.jetbrains.annotations.TestOnly

class GameConfiguration(
    val name: String,
    val pet: (() -> Monster)? = null
){

    var wizardMode = false

    companion object {
        @TestOnly
        fun dummy() = GameConfiguration("Bilbo Baggings")
    }
}
