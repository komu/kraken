package dev.komu.kraken.model.region

import dev.komu.kraken.definitions.ProbabilityDistribution
import dev.komu.kraken.definitions.betweenLevels
import dev.komu.kraken.model.Game
import dev.komu.kraken.model.region.generators.MazeRegionGenerator
import dev.komu.kraken.model.region.generators.RoomFirstRegionGenerator
import dev.komu.kraken.service.region.RegionLoader
import dev.komu.kraken.utils.Probability
import dev.komu.kraken.utils.randomInt
import java.util.*

class World(val game: Game) {
    private val loadedRegions = mutableMapOf<String, Region>()
    private val regions = ArrayList<RegionInfo>()
    private val mazeProbability = Probability(5)

    init {
        regions.add(RegionInfo("start", 0, random = false))

        for (n in 1..30)
            regions.add(RegionInfo("level$n", n, random = true))

        regions.add(RegionInfo("end", 31, random = false))

        for ((previous, next) in regions.zipWithNext()) {
            previous.next = next
            next.previous = previous
        }
    }

    fun getRegion(name: String): Region =
        loadedRegions.getOrPut(name) {
            initRegion(name)
        }

    private fun initRegion(name: String): Region {
        val info = regions.find { it.id == name } ?: throw IllegalArgumentException("unknown region <$name>")
        val region = loadRegion(info)
        addRandomCreatures(region)
        addRandomItems(region)
        return region
    }

    private fun loadRegion(info: RegionInfo) =
        if (info.random) {
            val generator = if (mazeProbability.check()) MazeRegionGenerator else RoomFirstRegionGenerator
            generator.generate(this, info.id, info.level, info.previous?.id, info.next?.id)
        } else
            RegionLoader(this).loadRegion(info)

    private fun addRandomCreatures(region: Region) {
        if (region.level == 0)
            return

        val monsterCount = randomInt(1, 1 + 2 * region.level)
        val emptyCells = region.getCellsForItemsAndCreatures()

        repeat(monsterCount) {
            val creatures = game.objectFactory.randomSwarm(region.level, game.player.level)
            if (emptyCells.isEmpty())
                return

            val cells = emptyCells.randomElement().cellsNearestFirst().filter { it.canMoveInto() }

            for ((creature, target) in creatures.asSequence().zip(cells)) {
                emptyCells.remove(target)
                creature.cell = target
            }
        }
    }

    private fun addRandomItems(region: Region) {
        val emptyCells = region.getCellsForItemsAndCreatures()
        if (emptyCells.isEmpty())
            return

        val items = ProbabilityDistribution(game.objectFactory.instantiableItems.betweenLevels(0, region.level))
        repeat(randomInt(6)) {
            val item = items.randomItem().create()
            emptyCells.randomElement().items.add(item)
        }
    }
}
