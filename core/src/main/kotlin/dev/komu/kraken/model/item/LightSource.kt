package dev.komu.kraken.model.item

import dev.komu.kraken.model.common.Color

open class LightSource(name: String): Item(name) {

    init {
        letter = '~'
        color = Color.YELLOW
    }

    override val lighting = 100
}
