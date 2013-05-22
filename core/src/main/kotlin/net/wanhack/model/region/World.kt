/*
 * Copyright 2013 The Wanhack Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.wanhack.model.region

import java.util.ArrayList
import java.util.HashMap
import java.util.Random
import net.wanhack.model.Game
import net.wanhack.model.creature.Player
import net.wanhack.model.item.Item
import net.wanhack.service.ServiceProvider
import net.wanhack.service.config.ObjectDefinition
import net.wanhack.service.creature.CreatureService
import net.wanhack.service.region.generators.MazeRegionGenerator
import net.wanhack.service.region.generators.RegionGenerator
import net.wanhack.service.region.generators.RoomFirstRegionGenerator
import net.wanhack.utils.Probability
import net.wanhack.utils.logger

class World(val game: Game) {
    private val loadedRegions = HashMap<String, Region>()
    private val regions = ArrayList<RegionInfo>()
    private val random = Random()
    private val mazeProbability = Probability(5);

    {
        addNonRandomRegion(0, "start")
        addRandomRegion(1, "level1")
        addRandomRegion(2, "level2")
        addRandomRegion(3, "level3")
        addRandomRegion(4, "level4")
        addRandomRegion(5, "level5")
        addRandomRegion(6, "level6")
        addRandomRegion(7, "level7")
        addRandomRegion(8, "level8")
        addRandomRegion(9, "level9")
        addRandomRegion(10, "level10")
        addRandomRegion(11, "level11")
        addRandomRegion(12, "level12")
        addRandomRegion(13, "level13")
        addRandomRegion(14, "level14")
        addRandomRegion(15, "level15")
        addRandomRegion(16, "level16")
        addRandomRegion(17, "level17")
        addRandomRegion(18, "level18")
        addRandomRegion(19, "level19")
        addRandomRegion(20, "level20")
        addRandomRegion(21, "level21")
        addRandomRegion(22, "level22")
        addRandomRegion(23, "level23")
        addRandomRegion(24, "level24")
        addRandomRegion(25, "level25")
        addRandomRegion(26, "level26")
        addRandomRegion(27, "level27")
        addRandomRegion(28, "level28")
        addRandomRegion(29, "level29")
        addRandomRegion(30, "level30")
        addNonRandomRegion(31, "end")

        var previous: RegionInfo? = null
        for (region in regions) {
            val prev = previous
            if (prev != null) {
                prev.next = region
                region.previous = prev
            }

            previous = region
        }
    }

    private fun addRandomRegion(level: Int, id: String) {
        regions.add(RegionInfo(id, level, true))
    }

    private fun addNonRandomRegion(level: Int, id: String) {
        regions.add(RegionInfo(id, level, false))
    }

    fun getRegion(player: Player, name: String): Region =
        loadedRegions.getOrPut(name) {
            initRegion(player, name)
        }

    private fun initRegion(player: Player, name: String): Region {
        val info = getRegionInfo(name)
        val region = loadRegion(info)
        addRandomCreatures(player, region)
        addRandomItems(region)
        return region
    }

    private fun loadRegion(info: RegionInfo): Region =
        if (info.random) {
            val up = info.previous?.id
            val down = info.next?.id

            getRegionGenerator().generate(this, info.id, info.level, up, down)
        } else {
            ServiceProvider.regionLoader.loadRegion(this, info)
        }

    private fun getRegionInfo(id: String): RegionInfo =
        regions.find { id == it.id } ?: throw IllegalArgumentException("unknown region <$id>")

    private fun getRegionGenerator(): RegionGenerator =
        if (mazeProbability.check())
            MazeRegionGenerator
        else
            RoomFirstRegionGenerator

    private fun addRandomCreatures(player: Player, region: Region) {
        if (region.level == 0)
            return

        val monsterCount = 1 + random.nextInt(2 * region.level)
        val emptyCells = region.getCellsForItemsAndCreatures()
        val creatureService = CreatureService.instance

        for (i in 1..monsterCount) {
            val creatures = creatureService.randomSwarm(region.level, player.level)
            if (emptyCells.empty)
                return

            val cell = emptyCells.randomElement()
            val cells = cell.matchingCellsNearestFirst { it.canMoveInto(true) }
            for (creature in creatures)
                if (cells.hasNext()) {
                    val target = cells.next()
                    emptyCells.remove(target)
                    creature.cell = target
                }
        }
    }
    private fun addRandomItems(region: Region) {
        val minItemLevel = 0
        val maxItemLevel = region.level
        val itemCount = random.nextInt(6)

        val emptyCells = region.getCellsForItemsAndCreatures()
        for (i in 1..itemCount) {
            if (emptyCells.empty)
                return

            val item = randomItem(minItemLevel, maxItemLevel)

            emptyCells.randomElement().items.add(item)
        }
    }

    private fun randomItem(minLevel: Int, maxLevel: Int): Item {
        val defs = ServiceProvider.objectFactory.getAvailableDefinitionsForClass(javaClass<Item>())
        return random(defs, minLevel, maxLevel).create()
    }

    private fun random<T>(defs: List<ObjectDefinition<T>>, minLevel: Int, maxLevel: Int): ObjectDefinition<T> {
        var probabilitySum = 0
        val probabilities = ArrayList<DefProbability<T>>(defs.size)
        for (od in defs) {
            val level = od.level
            if (level == null || (level >= minLevel && level <= maxLevel)) {
                val probability = od.probability ?: 100
                probabilities.add(DefProbability(od, probability, level))
                probabilitySum += probability
            }
        }

        var item = random.nextInt(probabilitySum)
        for (dp in probabilities) {
            if (dp.level == null || (dp.level >= minLevel && dp.level <= maxLevel)) {
                if (item < dp.probability)
                    return dp.def

                item -= dp.probability
            }
        }

        throw RuntimeException("could not randomize definition")
    }

    class object {
        private val log = javaClass<World>().logger()

        class DefProbability<T>(val def: ObjectDefinition<T>, val probability: Int, val level: Int?)
    }
}