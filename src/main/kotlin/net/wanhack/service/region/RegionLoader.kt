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

package net.wanhack.service.region

import net.wanhack.model.creature.Creature
import net.wanhack.model.item.Item
import net.wanhack.model.region.CellType
import net.wanhack.model.region.Region
import net.wanhack.model.region.RegionInfo
import net.wanhack.model.region.World
import net.wanhack.service.config.ObjectFactory
import java.io.*
import java.util.ArrayList
import java.util.HashMap
import net.wanhack.utils.logger

class RegionLoader(val objectFactory: ObjectFactory) {

    fun loadRegion(world: World, info: RegionInfo): Region =
        try {
            loadRegionImpl(world, info)
        } catch (e: IOException) {
            throw RegionLoadingException(e)
        }

    private fun loadRegionImpl(world: World, info: RegionInfo): Region {
        val reader = openReader("/regions/${info.id}.region")
        try
        {
            val regionAliases = HashMap<String, String>()
            val next = info.next
            if (next != null)
                regionAliases["%next"] = next.id

            val previous = info.previous
            if (previous != null)
                regionAliases["%previous"] = previous.id

            var seenDirective = false
            var y = 0
            val lines = ArrayList<String>()
            var rows = 0
            var cols = 0

            for (str in reader.lineIterator()) {
                if (!str.startsWith(";") && !(str == "" && seenDirective)) {
                    lines.add(str)
                    if (str.startsWith(":")) {
                        seenDirective = true
                    } else {
                        rows++
                        cols = Math.max(cols, str.length)
                    }
                }
            }

            val region = Region(world, info.id, info.level, cols + 1, rows + 1)
            seenDirective = false

            for (line in lines) {
                if (line.startsWith(":")) {
                    processDirective(region, line, regionAliases)
                    seenDirective = true
                } else if (!seenDirective) {
                    for (x in 0..line.length - 1) {
                        when (line[x]) {
                            '#'  -> region.getCell(x, y).setType(CellType.HALLWAY_FLOOR)
                            '<'  -> region.getCell(x, y).setType(CellType.STAIRS_UP)
                            '>'  -> region.getCell(x, y).setType(CellType.STAIRS_DOWN)
                            ' '  -> { }
                            else -> log.severe("unknown tile: ${line[x]}")
                        }
                    }
                    y++
                } else {
                    log.severe("invalid line: <$line>")
                }
            }
            return region
        } finally {
            reader.close()
        }
    }
    private fun processDirective(region: Region, line: String, regionAliases: Map<String, String>) {
        val tokens1 = CREATURE_PATTERN.getTokens(line)
        if (tokens1 != null) {
            val x = tokens1[0].toInt()
            val y = tokens1[1].toInt()
            val name = tokens1[2]
            val creature = objectFactory.create(javaClass<Creature>(), name)
            region.addCreature(creature, x, y)
            return
        }

        val tokens2 = ITEM_PATTERN.getTokens(line)
        if (tokens2 != null) {
            val x = tokens2[0].toInt()
            val y = tokens2[1].toInt()
            val name = tokens2[2]
            val item = objectFactory.create(javaClass<Item>(), name)
            region.addItem(x, y, item)
            return
        }

        val tokens3 = DOWN_PATTERN.getTokens(line)
        if (tokens3 != null) {
            val x = Integer.parseInt(tokens3[0])
            val y = Integer.parseInt(tokens3[1])
            val target = getRegion(tokens3[2], regionAliases)
            val location = tokens3[3]
            region.addPortal(x, y, target, location, false)
            return
        }

        val tokens4 = UP_PATTERN.getTokens(line)
        if (tokens4 != null) {
            val x = Integer.parseInt(tokens4[0])
            val y = Integer.parseInt(tokens4[1])
            val target = getRegion(tokens4[2], regionAliases)
            val location = tokens4[3]
            region.addPortal(x, y, target, location, true)
            return
        }

        val tokens6 = START_PATTERN.getTokens(line)
        if (tokens6 != null) {
            val name = tokens6[0]
            val x = Integer.parseInt(tokens6[1])
            val y = Integer.parseInt(tokens6[2])
            region.addStartPoint(name, x, y)
            return
        }

        val tokens7 = LIGHT_PATTERN.getTokens(line)
        if (tokens7 != null) {
            val x = Integer.parseInt(tokens7[0])
            val y = Integer.parseInt(tokens7[1])
            val lightPower = Integer.parseInt(tokens7[2])
            region.getCell(x, y).lightPower = lightPower
            return
        }

        log.severe("Invalid directive: $line")
    }

    private fun openReader(path: String): BufferedReader =
        javaClass.getResourceAsStream(path)?.reader("UTF-8")?.buffered() ?: throw FileNotFoundException("classpath:" + path)

    class object {
        private val START_PATTERN = DirectivePattern(":start [str] [int],[int]")
        private val CREATURE_PATTERN = DirectivePattern(":creature [int],[int] [str]")
        private val ITEM_PATTERN = DirectivePattern(":item [int],[int] [str]")
        private val DOWN_PATTERN = DirectivePattern(":portal down [int],[int] [str] [str]")
        private val UP_PATTERN = DirectivePattern(":portal up [int],[int] [str] [str]")
        private val LIGHT_PATTERN = DirectivePattern(":light [int],[int] [int]")
        private val log = javaClass<RegionLoader>().logger()

        private fun getRegion(name: String, aliases: Map<String, String>): String =
            aliases[name] ?: name
    }
}
