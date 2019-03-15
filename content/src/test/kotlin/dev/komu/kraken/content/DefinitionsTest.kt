package dev.komu.kraken.content

import org.junit.jupiter.api.Test

class DefinitionsTest {

    @Test
    fun loadDefinitions() {
        for (def in Weapons.items)
            def.create()

        for (def in Items.items)
            def.create()

        for (def in Creatures.monsters)
            def.create()
    }
}
