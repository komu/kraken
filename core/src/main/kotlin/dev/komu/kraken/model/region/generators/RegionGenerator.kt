package dev.komu.kraken.model.region.generators

import dev.komu.kraken.model.region.Region
import dev.komu.kraken.model.region.World

interface RegionGenerator {
    fun generate(world: World, name: String, level: Int, up: String?, down: String?): Region
}
