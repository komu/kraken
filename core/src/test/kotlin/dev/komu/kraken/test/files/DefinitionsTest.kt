package dev.komu.kraken.test.files

import dev.komu.kraken.definitions.Creatures
import dev.komu.kraken.definitions.Items
import dev.komu.kraken.definitions.Weapons
import org.junit.jupiter.api.Test

class DefinitionsTest {

    @Test
    fun loadDefinitions() {
        for (def in Weapons.itemDefinitions)
            def.create()

        for (def in Items.itemDefinitions)
            def.create()

        for (def in Creatures.creatureDefinitions)
            def.create()
    }
}
