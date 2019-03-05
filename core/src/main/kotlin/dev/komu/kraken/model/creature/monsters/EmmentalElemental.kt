package dev.komu.kraken.model.creature.monsters

import dev.komu.kraken.definitions.Items
import dev.komu.kraken.model.creature.Monster
import dev.komu.kraken.utils.Probability

class EmmentalElemental(name: String): Monster(name) {

    override fun createCorpse() =
        if (Probability.check(50)) Items.chunkOfCheese.create() else Items.bigChunkOfCheese.create()
}
